package com.quicklink.javalinfly.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicklink.javalinfly.annotation.OpenApiProperty;
import com.quicklink.javalinfly.openapi.model.Schema;
import com.quicklink.javalinfly.processor.round.MessagerRound;
import com.quicklink.javalinfly.processor.utils.ProcessorUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.jetbrains.annotations.NotNull;

public class OpenApiUtil {

  private final Types typeUtils;
  private final Elements elementUtils;
  private final MessagerRound messager;

  public OpenApiUtil(Types typeUtils, Elements elementUtils, MessagerRound messager) {
    this.typeUtils = typeUtils;
    this.elementUtils = elementUtils;
    this.messager = messager;
  }

  public Schema classToSchema(Map<String, Schema> schemas, TypeMirror typeMirror, String path, boolean request, boolean createSchema) {
    messager.warning("Analyzing schema '%s' of path '%s'", typeMirror.toString(), path);


    if(ProcessorUtil.getClassNameWithoutAnnotations(typeMirror).startsWith("java.")) {
      return Schema.builder().type("string").example("Class: " + typeMirror.toString()).build();
    }


    ProcessorUtil.asTypeElement(this.typeUtils, typeMirror);
    if (typeMirror.getKind() == TypeKind.TYPEVAR) {
      typeMirror = ((TypeVariable)typeMirror).getUpperBound();
    }

    if (!(typeMirror instanceof DeclaredType declaredType)) {
      throw new IllegalArgumentException("Unsupported type: " + typeMirror);
    } else {
      TypeElement classElement = (TypeElement) declaredType.asElement();
      Schema schema = new Schema();
      String nameClass = classElement.getSimpleName().toString();
      if (schemas.containsKey(nameClass)) {
        return Schema.builder().$ref(Schema.schemaRef(nameClass)).build();
      } else {
        if (createSchema) {
          schemas.put(nameClass, schema);
        }

        schema.description = "A JSON object containing a generic class information";
        TypeMirror keyType;
        if (this.isCollection(classElement)) {
          schema.type = "array";
          keyType = this.getGenericType(declaredType, 0);
          if (keyType != null) {
            this.typeUtils.asElement(keyType);
            schema.items = this.classToSchema(schemas, keyType, path, request, false);
          } else {
            schema.items = Schema.builder().type("object").description("Unknown type").build();
          }

          return schema;
        } else if (this.isMap(classElement)) {
          schema.type = "object";
          keyType = this.getGenericType(declaredType, 0);
          TypeMirror valueType = this.getGenericType(declaredType, 1);

          assert keyType != null;

          if (!ProcessorUtil.getClassNameWithoutAnnotations(keyType).equals("java.lang.String")) {
            throw new IllegalStateException("Invalid map at path " + path + ", key must be a String");
          } else {
            schema.additionalProperties = this.classToSchema(schemas, valueType, path, request, false);
            return schema;
          }
        } else {
          schema.type = "object";
          schema.description = "This is the class " + classElement.getSimpleName();
          schema.properties = new HashMap<>();
          List<String> required = new ArrayList<>();
          List<? extends Element> members = this.elementUtils.getAllMembers(classElement);
          for (Element member : members) {
            if (member.getKind() != ElementKind.FIELD) continue;

            VariableElement variableElement = (VariableElement) member;
            JsonIgnore jsonIgnore = variableElement.getAnnotation(JsonIgnore.class);
            if (jsonIgnore != null) continue;

            JsonProperty jsonProperty = variableElement.getAnnotation(JsonProperty.class);
            if (jsonProperty != null && (request && jsonProperty.access() == JsonProperty.Access.READ_ONLY || !request && jsonProperty.access() == JsonProperty.Access.WRITE_ONLY)) {
              continue;
            }

            if (variableElement.getAnnotation(NotNull.class) != null) {
              required.add(variableElement.getSimpleName().toString());
            }

            OpenApiProperty openApiProperty = variableElement.getAnnotation(OpenApiProperty.class);
            String exampleProperty = openApiProperty != null ? openApiProperty.defaultValue() : null;
            TypeMirror returnType = variableElement.asType();
            Schema fieldSchema = this.typeMirrorToSchema(schemas, returnType, path, request);
            if (exampleProperty != null) {
              fieldSchema.example = exampleProperty;
            }

            schema.properties.put(variableElement.getSimpleName().toString(), fieldSchema);
          }

          if (!required.isEmpty()) {
            schema.required = required;
          }

          return schema;
        }
      }
    }
  }

  private Schema typeMirrorToSchema(Map<String, Schema> schemas, TypeMirror typeMirror, String path, boolean request) {
    switch (typeMirror.getKind()) {
      case BOOLEAN:
        return Schema.builder().type("boolean").build();
      case INT:
      case LONG:
        return Schema.builder().type("integer").build();
      case FLOAT:
      case DOUBLE:
        return Schema.builder().type("number").build();
      case DECLARED:
        String typeName = ProcessorUtil.getClassNameWithoutAnnotations(typeMirror);
        if (typeName.equals("java.lang.String")) {
          return Schema.builder().type("string").build();
        } else if (typeName.equals("java.util.UUID")) {
          return Schema.builder().type("string").format("uuid").build();
        } else {
          Element returnTypeElement = this.typeUtils.asElement(typeMirror);
          if (returnTypeElement instanceof TypeElement && this.isEnum((TypeElement) returnTypeElement)) {
            return Schema.builder().type("string")._enum(this.getEnumValues((TypeElement) returnTypeElement)).build();
          }

          return this.classToSchema(schemas, typeMirror, path, request, false);
        }
      default:
        this.messager.error("Unsupported field type %s of path %s", new Object[]{typeMirror.toString(), path});
        return null;
    }
  }

  private TypeMirror getGenericType(DeclaredType declaredType, int index) {
    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    if (typeArguments.size() > index) {
      TypeMirror typeArgument = typeArguments.get(index);
      if (typeArgument.getKind() == TypeKind.TYPEVAR) {
        TypeVariable typeVariable = (TypeVariable) typeArgument;
        if (typeVariable.getUpperBound().getKind() == TypeKind.DECLARED) {
          return typeVariable.getUpperBound();
        } else {
          // Handle the case where the upper bound is not a declared type
          // For now, we return the upper bound directly, but this can be customized further
          return typeVariable.getUpperBound();
        }
      }
      return typeArgument;
    }
    return null;
  }

  private boolean isCollection(TypeElement classElement) {
    return typeUtils.isAssignable(classElement.asType(),
        elementUtils.getTypeElement("java.util.List").asType()) ||
        typeUtils.isAssignable(classElement.asType(),
            elementUtils.getTypeElement("java.util.Set").asType());
  }

  private boolean isMap(TypeElement classElement) {
    return typeUtils.isAssignable(classElement.asType(),
        elementUtils.getTypeElement("java.util.Map").asType());
  }

  private boolean isEnum(TypeElement classElement) {
    return classElement.getKind() == ElementKind.ENUM;
  }

  private List<String> getEnumValues(TypeElement classElement) {
    List<String> values = new ArrayList<>();
    for (Element element : classElement.getEnclosedElements()) {
      if (element.getKind() == ElementKind.ENUM_CONSTANT) {
        values.add(element.getSimpleName().toString());
      }
    }
    return values;
  }
}
