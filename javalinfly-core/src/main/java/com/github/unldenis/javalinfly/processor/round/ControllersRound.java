package com.github.unldenis.javalinfly.processor.round;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.unldenis.javalinfly.annotation.Body;
import com.github.unldenis.javalinfly.annotation.Controller;
import com.github.unldenis.javalinfly.annotation.Delete;
import com.github.unldenis.javalinfly.FileResponse;
import com.github.unldenis.javalinfly.annotation.Get;
import com.github.unldenis.javalinfly.annotation.Path;
import com.github.unldenis.javalinfly.annotation.Post;
import com.github.unldenis.javalinfly.annotation.Put;
import com.github.unldenis.javalinfly.annotation.Query;
import com.github.unldenis.javalinfly.Response;
import com.github.unldenis.javalinfly.ResponseType;
import com.github.unldenis.javalinfly.SuccessResponse;
import com.github.unldenis.javalinfly.openapi.OpenApiUtil;
import com.github.unldenis.javalinfly.openapi.model.Schema;
import com.github.unldenis.javalinfly.processor.Round;
import com.github.unldenis.javalinfly.processor.utils.ProcessorUtil;
import com.github.unldenis.javalinfly.processor.utils.StringUtils;
import io.javalin.http.UploadedFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ControllersRound extends Round {

  private final Types typeUtils;
  private final Elements elementUtils;

  private final MessagerRound messager;
  private final RoundEnvironment roundEnv;
  private final TypeMirror rolesTypeMirror;


  public Map<String, ExecutableElement> selectedRoles = new HashMap<>();

  // generator
  public final Set<String> endpoints = new HashSet<>();
  public final Set<String> handlersField = new HashSet<>();
  public final List<String> openApiStatements = new ArrayList<>();


  public ControllersRound(Types typeUtils, Elements elementUtils, MessagerRound messager,
      RoundEnvironment roundEnv,
      TypeMirror rolesTypeMirror) {
    this.typeUtils = typeUtils;
    this.elementUtils = elementUtils;
    this.messager = messager;
    this.roundEnv = roundEnv;
    this.rolesTypeMirror = rolesTypeMirror;
  }


  @Override
  protected void run() {
    Map<String, Schema> schemaMap = new HashMap<>();
    schemaMap.put("CustomType", Schema.builder().type("string").build());
    schemaMap.put("SuccessResponse", Schema.builder().type("object").build());

    OpenApiUtil openApiUtil = new OpenApiUtil(typeUtils, elementUtils, messager);

    Set<? extends Element> controllers = roundEnv.getElementsAnnotatedWith(Controller.class);

    // Iterate over all @Controller annotated elements
    for (Element elementController : controllers) {

      // Check if a class has been annotated with @Controller
      if (elementController.getKind() != ElementKind.CLASS) {
        messager.error(elementController, "Only classes can be annotated with @%s",
            Controller.class.getSimpleName());
        return;
      }

      TypeElement annotatedElement = (TypeElement) elementController;

      Controller controller = annotatedElement.getAnnotation(Controller.class);

      String varDecl = registerConstructors(annotatedElement);
      if (varDecl == null) {
        messager.error(annotatedElement, "Class %s missing an empty constructor",
            annotatedElement.getQualifiedName());
        return;
      }

      for (Element enclosedElement : annotatedElement.getEnclosedElements()) {
        if (enclosedElement.getKind() != ElementKind.METHOD) {
          continue;
        }
        if (!enclosedElement.getModifiers().contains(Modifier.PUBLIC)) {
          continue;
        }

        ExecutableElement executableElement = (ExecutableElement) enclosedElement;

        Get get = executableElement.getAnnotation(Get.class);
        Post post = executableElement.getAnnotation(Post.class);
        Put put = executableElement.getAnnotation(Put.class);
        Delete delete = executableElement.getAnnotation(Delete.class);

        String handlerType = null;
        String responseType = null;
        String[] handlerRoles;
        String summary;
        String[] handlerTags = null;
        ResponseType handlerResponseType = null;
        if (get != null) {
          handlerType = "GET";
          handlerResponseType = get.responseType();
          responseType = get.responseType().compiled();
          handlerRoles = get.roles();
          summary = get.summary();
          handlerTags = get.tags();
        } else if (post != null) {
          handlerType = "POST";
          handlerResponseType = post.responseType();
          responseType = post.responseType().compiled();
          handlerRoles = post.roles();
          summary = post.summary();
          handlerTags = post.tags();
        } else if (put != null) {
          handlerType = "PUT";
          handlerResponseType = put.responseType();
          responseType = put.responseType().compiled();
          handlerRoles = put.roles();
          summary = put.summary();
          handlerTags = put.tags();
        } else if (delete != null) {
          handlerType = "DELETE";
          handlerResponseType = delete.responseType();
          responseType = delete.responseType().compiled();
          handlerRoles = delete.roles();
          summary = delete.summary();
          handlerTags = delete.tags();
        } else {
          return;
        }



        String rolesStr = "";
        if (handlerRoles.length > 0) {
          for (String role : handlerRoles) {
            selectedRoles.put(role, executableElement);
          }

          rolesStr = ", new RouteRole[]{";
          rolesStr += String.join(",", Arrays.stream(handlerRoles)
              .map(roleName -> rolesTypeMirror.toString() + "." + roleName)
              .collect(Collectors.toSet()));
          rolesStr += "}";
        }

        // ** logic

        StringBuilder endpointPath = new StringBuilder(controller.path());

        List<String> parametersCall = new ArrayList<>();
        parametersCall.add("ctx");
        List<String> parametersDecl = new ArrayList<>();

        List<String> pathParameters = new ArrayList<>();
        List<String> queryParameters = new ArrayList<>();

        String bodySchema = null;
        String returnOkSchema = null;
        String returnErrSchema = null;


        TypeMirror returnType = executableElement.getReturnType();
        TypeElement returnTypeElement = ProcessorUtil.asTypeElement(typeUtils, returnType);

        if(returnTypeElement.getQualifiedName().toString().equals(Response.class.getName())) {
          var returnTypeGeneric = ProcessorUtil.getGenericTypes(returnType);
          TypeMirror returnTypeOk = returnTypeGeneric.get(0);
          TypeMirror returnTypeErr = returnTypeGeneric.get(1);

          switch (handlerResponseType) {
            case JSON:
              if(returnTypeOk.getKind() != TypeKind.DECLARED  || returnTypeOk.toString().startsWith("java.lang.")) {
                messager.error(executableElement, "Endpoint method must return a valid success object");
                return;
              }
              if(returnTypeErr.getKind() != TypeKind.DECLARED || returnTypeErr.toString().startsWith("java.lang.")) {
                messager.error(executableElement, "Endpoint method must return a valid error object");
                return;
              }
              // openapi

              // ok
            {
              TypeElement typeOk = ProcessorUtil.asTypeElement(typeUtils, returnTypeOk);
              var schema = openApiUtil.classToSchema(schemaMap,
                  returnTypeOk, endpointPath.toString(), true, true);

              returnOkSchema = typeOk.getSimpleName().toString();
            }

            // err
            {
              TypeElement typeErr = ProcessorUtil.asTypeElement(typeUtils, returnTypeErr);
              var schema = openApiUtil.classToSchema(schemaMap,
                  returnTypeErr, endpointPath.toString(), true, true);

              returnErrSchema = typeErr.getSimpleName().toString();
            }

            break;
            case HTML:
            case STRING:
              if(!returnTypeOk.toString().equals(String.class.getName()) || !returnTypeErr.toString().equals(String.class.getName())) {
                messager.error(executableElement, "Endpoint method must return a Response<String, String>");
                return;
              }
              break;
            case FILE:
              if(returnTypeOk.getKind() != TypeKind.DECLARED  || !returnTypeOk.toString().equals(
                  FileResponse.class.getName())) {
                messager.error(executableElement, "Endpoint response must return a FileResponse");
                return;
              }

              // openapi

              // ok
              {
                returnOkSchema = "@FileResponse";
              }
              // err
              {
                TypeElement typeErr = ProcessorUtil.asTypeElement(typeUtils, returnTypeErr);
                var schema = openApiUtil.classToSchema(schemaMap,
                    returnTypeErr, endpointPath.toString(), true, true);

                returnErrSchema = typeErr.getSimpleName().toString();
              }
          }
        }
        else if(returnTypeElement.getQualifiedName().toString().equals(SuccessResponse.class.getName())) {
          var returnTypeGeneric = ProcessorUtil.getGenericTypes(returnType);
          TypeMirror returnTypeErr = returnTypeGeneric.get(0);

          switch (handlerResponseType) {
            case JSON:
              if (returnTypeErr.getKind() != TypeKind.DECLARED || returnTypeErr.toString()
                  .startsWith("java.lang.")) {
                messager.error(executableElement,
                    "Endpoint method must return a valid error object");
                return;
              }
              // openapi

              // ok
            {
              returnOkSchema = "SuccessResponse";
            }
            // err
            {
              TypeElement typeErr = ProcessorUtil.asTypeElement(typeUtils, returnTypeErr);
              var schema = openApiUtil.classToSchema(schemaMap,
                  returnTypeErr, endpointPath.toString(), true, true);

              returnErrSchema = typeErr.getSimpleName().toString();
            }

            break;
            case HTML:
            case STRING:
              if (!returnTypeErr.toString().equals(String.class.getName())) {
                messager.error(executableElement,
                    "Endpoint method must return a SuccessResponse<String>");
                return;
              }
              break;
            case FILE:
              messager.error(executableElement, "File response is not supported in SuccessResponse");
              return;
          }
        }
        else {
          messager.error(executableElement, "Endpoint method must return a Response or SuccessResponse");
          return;
        }

//        if (executableElement.getReturnType().getKind() != TypeKind.DECLARED
//            || !returnTypeElement.getQualifiedName().toString().equals(Response.class.getName())) {
//        }



        for (VariableElement variableElement : executableElement.getParameters()) {

          String nameParameter = variableElement.getSimpleName().toString();
          String typeParameter = variableElement.asType().toString();
          String classParameter = typeParameter.split("<")[0];

          Body body = variableElement.getAnnotation(Body.class);
          if (body != null) {

            parametersCall.add(nameParameter);

            if (body.customType()) {
              if (!typeParameter.equals(String.class.getName())) {
                messager.error(variableElement,
                    "Body parameter '%s' must be a String since is customType",
                    nameParameter);
                return;
              }
              parametersDecl.add(String.format("String %s = ctx.body();\n", nameParameter));

              // openapi
              bodySchema = "CustomType";
            } else {
              if(typeParameter.equals("io.javalin.http.UploadedFile")) {
                parametersDecl.add(
                    String.format("%s %s =com.github.unldenis.javalinfly.ContextExt.uploadedFile(ctx);\n", typeParameter,
                        nameParameter));

                bodySchema = "$UploadedFile";

              } else {
                parametersDecl.add(
                    String.format("%s %s = (%s) ctx.bodyAsClass(%s.class);\n", typeParameter,
                        nameParameter, typeParameter, classParameter));

                // openapi
                TypeElement typeBodyName = ProcessorUtil.asTypeElement(typeUtils,
                    variableElement.asType());
                Schema schema = openApiUtil.classToSchema(schemaMap,
                    variableElement.asType(),
                    endpointPath.toString(), true, true);

                bodySchema = typeBodyName.getSimpleName().toString();
              }


            }


          }

          Path path = variableElement.getAnnotation(Path.class);
          if (path != null) {

            endpointPath.append("/{").append(nameParameter).append("}");
            parametersCall.add(nameParameter);

            parametersDecl.add(String.format("String %s = ctx.pathParam(\"%s\");\n", nameParameter,
                nameParameter));

            pathParameters.add("\"" + nameParameter + "\"");
          }

          Query query = variableElement.getAnnotation(Query.class);
          if (query != null) {

            parametersCall.add(nameParameter);

            parametersDecl.add(String.format("String %s = ctx.queryParam(\"%s\");\n", nameParameter,
                nameParameter));

            queryParameters.add("\"" + nameParameter + "\"");

          }
        }



        endpoints.add(
            "javalin.addHandler(HandlerType." + handlerType + ",config.pathPrefix + \""
                + endpointPath.toString() + "\", ctx -> {\n" +
                String.join("", parametersDecl) +
                String.format("var response = %s.%s(%s);\n", varDecl,
                    executableElement.getSimpleName(), String.join(",", parametersCall)) +
                responseType + "\n" +
                "\n} " + rolesStr + ");\n"
        );

        openApiStatements.add(
            String.format(
                "            openApiTranslator.addPath(\"%s\", \"%s\", %s, \"%s\", %s, %s, %s, %s, %s, %s, ResponseType.%s);\n",
                endpointPath.toString(),
                handlerType,
                StringUtils.arrayToJavaCode(handlerRoles),
                summary,
                pathParameters.isEmpty() ? "Collections.emptyList()"
                    : String.format("Arrays.asList(%s)", String.join(",", pathParameters)),
                queryParameters.isEmpty() ? "Collections.emptyList()"
                    : String.format("Arrays.asList(%s)", String.join(",", queryParameters)),
                StringUtils.arrayToJavaCode(handlerTags),
                /*body*/ bodySchema == null ? null : "\"" + bodySchema + "\"",
                /* ok */ returnOkSchema == null ? null : "\"" + returnOkSchema + "\"",
                /* err */ returnErrSchema == null ? null : "\"" + returnErrSchema + "\"",
                handlerResponseType.name()
            ));
      }
    }

    var MAPPER = new ObjectMapper();
    MAPPER.setSerializationInclusion(Include.NON_NULL);
    try {
      String schemasEncoded = Base64.getEncoder()
          .encodeToString(MAPPER.writeValueAsString(schemaMap).getBytes());
      openApiStatements.add(0, "            openApiTranslator.decodeSchemas(\"" + schemasEncoded + "\");\n");
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private @Nullable String registerConstructors(@NotNull TypeElement classTree) {
    for (var member : classTree.getEnclosedElements()) {
      if (!(member instanceof ExecutableElement)) {
        continue;
      }
      ExecutableElement executableElement = (ExecutableElement) member;

      if (!executableElement.getModifiers().contains(Modifier.PUBLIC)
          || executableElement.getKind() != ElementKind.CONSTRUCTOR) {
        continue;
      }

      if (executableElement.getParameters().isEmpty()) {
        String className = classTree.getQualifiedName().toString();
        String varDecl = ProcessorUtil.notCapitalize(className.replace(".", ""));
        this.handlersField.add(
            String.format("private %s %s = new %s();\n", className, varDecl, className));
        return varDecl;
      }

    }

    return null;
  }
}
