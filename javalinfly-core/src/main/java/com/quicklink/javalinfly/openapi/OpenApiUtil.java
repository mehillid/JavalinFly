package com.quicklink.javalinfly.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicklink.javalinfly.annotation.OpenApiProperty;
import com.quicklink.javalinfly.annotation.Post;
import com.quicklink.javalinfly.openapi.model.Schema;
import com.quicklink.javalinfly.processor.utils.Messager;
import com.quicklink.javalinfly.processor.utils.ProcessorUtil;
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

  public OpenApiUtil(Types typeUtils, Elements elementUtils) {
    this.typeUtils = typeUtils;
    this.elementUtils = elementUtils;
  }

  public Schema classToSchema(Map<String, Schema> schemas, TypeMirror typeMirror, String path,
      boolean request, boolean createSchema) {
    Messager.warning("Analyzing schema '%s' of path '%s'", typeMirror.toString(), path);

//
//    if(ProcessorUtil.getClassNameWithoutAnnotations(typeMirror).startsWith("java.")) {
//      return Schema.builder().type("string").example("Class: " + typeMirror.toString()).build();
//    }
    // if is primitive or string
    if (isKnownType(typeMirror)) {
      return this.typeMirrorToSchema(schemas, typeMirror, path, request);
    }

    ProcessorUtil.asTypeElement(this.typeUtils, typeMirror);
    if (typeMirror.getKind() == TypeKind.TYPEVAR) {
      typeMirror = ((TypeVariable) typeMirror).getUpperBound();
    }

    if (!(typeMirror instanceof DeclaredType declaredType)) {
      Messager.error("Unsupported type: " + typeMirror);
      return null;
    } else {
      TypeElement classElement = (TypeElement) declaredType.asElement();
      Schema schema = new Schema();
      String nameClass = classElement.getSimpleName().toString();

      Messager.warning("\tclassElement '%s'", classElement);
      if (schemas.containsKey(nameClass)) {
        return Schema.builder().$ref(Schema.schemaRef(nameClass)).build();
      } else {
        if (createSchema) {
          schemas.put(nameClass, schema);
        }

        schema.description = "A JSON object containing a generic class information";
        TypeMirror keyType;
        if (this.isObject(classElement)) {


          schema.type = "object";
          schema.description = "Unknown type";
          return schema;
//        } else if (typeName.startsWith("java.util.Map") || typeName.startsWith("java.util.HashMap")
//            || typeName.startsWith("java.util.LinkedHashMap")){
        } else if (isMap(typeMirror)){


          Messager.warning("\t\t** is map");
          schema.type = "object";
          keyType = this.getGenericType(declaredType, 0);
          TypeMirror valueType = this.getGenericType(declaredType, 1);

          if(createSchema) {
            schemas.remove(nameClass);
            nameClass += " " + ProcessorUtil.getTypeSimpleName(typeUtils, keyType) + "," + ProcessorUtil.getTypeSimpleName(typeUtils, valueType);
            schemas.put(nameClass, schema);
          }

          assert keyType != null;

          if (!ProcessorUtil.getClassNameWithoutAnnotations(keyType).equals("java.lang.String")) {
            throw new IllegalStateException(
                "Invalid map at path " + path + ", key must be a String");
          } else {
            schema.additionalProperties = this.classToSchema(schemas, valueType, path, request,
                false);
            return schema;
          }
        } else if (this.isCollection(typeMirror)) {
          schema.type = "array";
          keyType = this.getGenericType(declaredType, 0);


          if(createSchema) {
            schemas.remove(nameClass);
            nameClass += " " + ProcessorUtil.getTypeSimpleName(typeUtils, keyType);
            schemas.put(nameClass, schema);
          }

          if (keyType != null) {
            this.typeUtils.asElement(keyType);
            schema.items = this.classToSchema(schemas, keyType, path, request, false);
          } else {
            schema.items = Schema.builder().type("object").description("Unknown type").build();
          }

          return schema;
        } else {

          schema.type = "object";
          schema.description = "This is the class " + classElement.getSimpleName();
          schema.properties = new HashMap<>();
          List<String> required = new ArrayList<>();
          List<? extends Element> members = this.elementUtils.getAllMembers(classElement);
          for (Element member : members) {
            if (member.getKind() != ElementKind.FIELD) {
              continue;
            }

            VariableElement variableElement = (VariableElement) member;
            JsonIgnore jsonIgnore = variableElement.getAnnotation(JsonIgnore.class);
            if (jsonIgnore != null) {
              continue;
            }

            JsonProperty jsonProperty = variableElement.getAnnotation(JsonProperty.class);
            if (jsonProperty != null && (
                request && jsonProperty.access() == JsonProperty.Access.READ_ONLY
                    || !request && jsonProperty.access() == JsonProperty.Access.WRITE_ONLY)) {
              continue;
            }

            if (variableElement.getAnnotation(NotNull.class) != null) {
              required.add(variableElement.getSimpleName().toString());
            }

            OpenApiProperty openApiProperty = variableElement.getAnnotation(OpenApiProperty.class);
            String exampleProperty =
                openApiProperty != null ? openApiProperty.defaultValue() : null;
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

  private Schema typeMirrorToSchema(Map<String, Schema> schemas, TypeMirror typeMirror, String path,
      boolean request) {
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
        } else if (typeName.equals("java.lang.Integer") || typeName.equals("java.lang.Long")) {
          return Schema.builder().type("integer").build();
        } else if (typeName.equals("java.lang.Float") || typeName.equals("java.lang.Double")) {
          return Schema.builder().type("number").build();
        } else {
          Element returnTypeElement = this.typeUtils.asElement(typeMirror);
          if (returnTypeElement instanceof TypeElement && this.isEnum(
              (TypeElement) returnTypeElement)) {
            return Schema.builder().type("string")
                ._enum(this.getEnumValues((TypeElement) returnTypeElement)).build();
          }

          return this.classToSchema(schemas, typeMirror, path, request, false);
        }
      default:
        Messager.error("Unsupported field type %s of path %s",
            typeMirror.toString(), path);

        return null;
    }
  }

  private boolean isKnownType(TypeMirror typeMirror) {
    switch (typeMirror.getKind()) {
      case BOOLEAN, INT, LONG, FLOAT, DOUBLE:
        return true;
      case DECLARED:
        String typeName = ProcessorUtil.getClassNameWithoutAnnotations(typeMirror);
        switch (typeName) {
          case "java.lang.String", "java.util.UUID", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double" -> {
            return true;
          }
          default -> {
            Element returnTypeElement = this.typeUtils.asElement(typeMirror);
            if (returnTypeElement instanceof TypeElement && this.isEnum(
                (TypeElement) returnTypeElement)) {
              return true;
            }

            return false;
          }
        }
      default:
        return false;
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

  private boolean isCollection(TypeMirror tm) {
    TypeMirror list = typeUtils.erasure(elementUtils.getTypeElement("java.util.List").asType());
    TypeMirror set = typeUtils.erasure(elementUtils.getTypeElement("java.util.Set").asType());

    return typeUtils.isAssignable(tm, list) || typeUtils.isAssignable(tm, set);
  }


  public boolean isMap(TypeMirror tm) {
    TypeMirror map = typeUtils.erasure(elementUtils.getTypeElement("java.util.Map").asType());
    return typeUtils.isAssignable(tm, map);
  }

  private boolean isSubtypeOf(TypeMirror type, TypeMirror targetType) {
    if (typeUtils.isAssignable(type, targetType)) {
      return true;
    }

    if (type.getKind() != TypeKind.DECLARED) {
      return false;
    }

    DeclaredType declaredType = (DeclaredType) type;
    TypeElement typeElement = (TypeElement) declaredType.asElement();

    // Check if any of the superclasses or interfaces are assignable to targetType
    return checkSuperTypes(typeElement, targetType);
  }

  private boolean checkSuperTypes(TypeElement typeElement, TypeMirror targetType) {
    // Check superclasses
    TypeMirror superclass = typeElement.getSuperclass();
    if (superclass.getKind() != TypeKind.NONE && isSubtypeOf(superclass, targetType)) {
      return true;
    }

    // Check interfaces
    List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
    for (TypeMirror iface : interfaces) {
      if (isSubtypeOf(iface, targetType)) {
        return true;
      }
    }

    return false;
  }

  private boolean isObject(TypeElement classElement) {
    return typeUtils.isSameType(classElement.asType(),
        elementUtils.getTypeElement("java.lang.Object").asType());
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
