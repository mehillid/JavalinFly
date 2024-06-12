package com.github.unldenis.javalinfly.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.unldenis.javalinfly.OpenApiProperty;
import com.github.unldenis.javalinfly.openapi.model.Schema;
import com.github.unldenis.javalinfly.processor.utils.ProcessorUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class OpenApiUtil {

  private final Types typeUtils;
  private final Elements elementUtils;

  public OpenApiUtil(Types typeUtils, Elements elementUtils) {
    this.typeUtils = typeUtils;
    this.elementUtils = elementUtils;
  }

  private Schema classToSchema(Map<String, Schema> schemas, TypeElement classElement, String path, VariableElement field, List<? extends TypeMirror> methodGenericTypes, boolean request) {
    String nameClass = classElement.getQualifiedName().toString();

    if (schemas.containsKey(nameClass)) {
      return Schema.builder().$ref(Schema.schemaRef(nameClass))
          .build(); // Implementa il metodo setRef
    }

    Schema schema = new Schema();
    schema.description = "A JSON object containing a generic class information";

    // Handle List or Set
    if (isCollection(classElement)) {
      schema.type = "array";

      TypeMirror genericListType = ((DeclaredType) classElement.asType()).getTypeArguments().get(0);
      TypeElement genericElement = (TypeElement) typeUtils.asElement(genericListType);
      schema.items = (classToSchema(schemas, genericElement, path, null, null, request));
      return schema;
    } else {
      schema.type = "object";
      schema.description = "This is the class " + classElement.getSimpleName();

      if (isMap(classElement)) {
        // Handle Map
        DeclaredType mapType = (DeclaredType) classElement.asType();
        TypeMirror keyType = mapType.getTypeArguments().get(0);
        TypeMirror valueType = mapType.getTypeArguments().get(1);

        if (!keyType.toString().equals("java.lang.String")) {
          throw new IllegalStateException("Invalid map at path " + path + ", key must be a String");
        }

        TypeElement valueElement = (TypeElement) typeUtils.asElement(valueType);
        schema.additionalProperties = (classToSchema(schemas, valueElement, path, null, null, request));
        return schema;
      } else {
        schema.properties = new HashMap<>();
      }
    }

    List<String> required = new ArrayList<>();

    List<? extends Element> members = elementUtils.getAllMembers(classElement);

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

        OpenApiProperty openApiProperty = variableElement.getAnnotation(OpenApiProperty.class);
        String exampleProperty = openApiProperty != null ? openApiProperty.defaultValue() : null;

        if (!variableElement.asType().getKind().isPrimitive() && !variableElement.asType()
            .toString().startsWith("java.lang.")) {
          required.add(variableElement.getSimpleName().toString());
        }

        TypeMirror returnType = variableElement.asType();
        TypeElement returnTypeElement = (TypeElement) typeUtils.asElement(returnType);

        Schema fieldSchema;
        switch (returnType.getKind()) {
          case BOOLEAN:
            fieldSchema = Schema.builder().type("boolean").build();
            break;
          case INT:
          case LONG:
            fieldSchema = Schema.builder().type("integer").build();
            break;
          case FLOAT:
          case DOUBLE:
            fieldSchema = Schema.builder().type("number").build();
            break;
          case DECLARED:
            String typeName = returnType.toString();
            if (typeName.equals("java.lang.String")) {
              fieldSchema = Schema.builder().type("string").build();
            } else if (typeName.equals("java.util.UUID")) {
              fieldSchema = Schema.builder().type("string").format("uuid").build();
            } else if (isEnum(returnTypeElement)) {
              fieldSchema = Schema.builder().type("string")._enum(getEnumValues(returnTypeElement)).build();
            } else {
              List<? extends TypeMirror> genericTypes = ProcessorUtil.getGenericTypes(variableElement);
              fieldSchema = classToSchema(schemas, returnTypeElement, path, variableElement, genericTypes, request);
            }
            break;
          default:
            throw new IllegalArgumentException("Unsupported field type: " + returnType);
        }

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
