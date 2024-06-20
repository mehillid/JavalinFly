package com.github.unldenis.javalinfly.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.unldenis.javalinfly.FileResponse;
import com.github.unldenis.javalinfly.annotation.OpenApiProperty;
import com.github.unldenis.javalinfly.openapi.model.Schema;
import com.github.unldenis.javalinfly.processor.round.MessagerRound;
import com.github.unldenis.javalinfly.processor.utils.ProcessorUtil;
import io.javalin.http.UploadedFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  public Schema classToSchema(Map<String, Schema> schemas, TypeMirror typeMirror, String path,
      boolean request, boolean createSchema) {

    Element element = ProcessorUtil.asTypeElement(typeUtils, typeMirror);

    messager.warning("Path '%s', Type '%s', element '%s', kind '%s'", path, typeMirror, element,
        element.getKind().toString());
    if (typeMirror.getKind() == TypeKind.TYPEVAR) {
      typeMirror = ((TypeVariable) typeMirror).getUpperBound();
    }

    if (!(typeMirror instanceof DeclaredType)) {
      throw new IllegalArgumentException("Unsupported type: " + typeMirror);
    }

    DeclaredType declaredType = (DeclaredType) typeMirror;
    TypeElement classElement = (TypeElement) declaredType.asElement();

    Schema schema = new Schema();

    String nameClass = classElement.getSimpleName().toString();
    if (schemas.containsKey(nameClass)) {
      messager.warning("Key already contained...");

      return Schema.builder().$ref(Schema.schemaRef(nameClass)).build();
    }
    if (createSchema) {
      schemas.put(nameClass, schema);
    }

    schema.description = "A JSON object containing a generic class information";

    if (isCollection(classElement)) {
      schema.type = "array";
      TypeMirror genericListType = getGenericType(declaredType, 0);
      if (genericListType != null) {
        Element genericElement = typeUtils.asElement(genericListType);
        messager.warning("Generic mirror %s, element %s", genericListType, genericElement);
        schema.items = classToSchema(schemas, genericListType, path, request, false);
      } else {
        schema.items = Schema.builder().type("object").description("Unknown type").build();
      }
      return schema;
    } else if (isMap(classElement)) {
      schema.type = "object";
      TypeMirror keyType = getGenericType(declaredType, 0);
      TypeMirror valueType = getGenericType(declaredType, 1);

      assert keyType != null;
      if (!keyType.toString().equals("java.lang.String")) {
        throw new IllegalStateException("Invalid map at path " + path + ", key must be a String");
      }
      schema.additionalProperties = classToSchema(schemas, valueType, path, request, false);
      return schema;
    } else {
      schema.type = "object";
      schema.description = "This is the class " + classElement.getSimpleName();
      schema.properties = new HashMap<>();
    }

    List<String> required = new ArrayList<>();
    List<? extends Element> members = elementUtils.getAllMembers(classElement);
    messager.warning("Starting processing variables...");

    for (Element member : members) {
      if (member.getKind() == ElementKind.FIELD) {
        VariableElement variableElement = (VariableElement) member;
        JsonIgnore jsonIgnore = variableElement.getAnnotation(JsonIgnore.class);
        if (jsonIgnore != null) {
          continue;
        }

        JsonProperty jsonProperty = variableElement.getAnnotation(JsonProperty.class);
        if (jsonProperty != null) {
          if (request && jsonProperty.access() == JsonProperty.Access.READ_ONLY) {
            continue;
          }

          if (!request && jsonProperty.access() == JsonProperty.Access.WRITE_ONLY) {
            continue;
          }
        }
//
        if(variableElement.getAnnotation(NotNull.class) != null) {
          required.add(variableElement.getSimpleName().toString());
        }

        OpenApiProperty openApiProperty = variableElement.getAnnotation(OpenApiProperty.class);
        String exampleProperty = openApiProperty != null ? openApiProperty.defaultValue() : null;

//        if (!variableElement.asType().getKind().isPrimitive() && !variableElement.asType()
//            .toString().startsWith("java.lang.")) {
//          required.add(variableElement.getSimpleName().toString());
//        }

        messager.warning("Variable '%s'", variableElement.getSimpleName().toString());

        TypeMirror returnType = variableElement.asType();
        Schema fieldSchema = typeMirrorToSchema(schemas, returnType, path, request);

        if (exampleProperty != null) {
          fieldSchema.example = exampleProperty;
        }

        schema.properties.put(variableElement.getSimpleName().toString(), fieldSchema);
      }
    }

    if (!required.isEmpty()) {
      schema.required = required;
    }

    return schema;
  }

  private Schema typeMirrorToSchema(Map<String, Schema> schemas, TypeMirror typeMirror, String path,
      boolean request) {
    messager.warning("typeMirrorToSchema, path '%s', typeMirror '%s'", path, typeMirror);

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
        messager.warning("TypeName UTIL '%s'", typeName);
        if (typeName.equals("java.lang.String")) {
          return Schema.builder().type("string").build();
        } else if (typeName.equals("java.util.UUID")) {
          return Schema.builder().type("string").format("uuid").build();
        } else {
          Element returnTypeElement = typeUtils.asElement(typeMirror);
          messager.warning("UTIL function return '%s', element '%s'", typeMirror, returnTypeElement);

          if (returnTypeElement instanceof TypeElement && isEnum((TypeElement) returnTypeElement)) {
            return Schema.builder().type("string")
                ._enum(getEnumValues((TypeElement) returnTypeElement)).build();
          } else {
            return classToSchema(schemas, typeMirror, path, request,false);
          }
        }
      default:
        messager.error("Unsupported field type %s of path %s", typeMirror.toString(), path);
//        throw new IllegalArgumentException("Unsupported field type: " + typeMirror);

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
