package com.github.unldenis.javalinfly.processor.round;

import com.github.unldenis.javalinfly.Body;
import com.github.unldenis.javalinfly.Controller;
import com.github.unldenis.javalinfly.Delete;
import com.github.unldenis.javalinfly.Get;
import com.github.unldenis.javalinfly.Path;
import com.github.unldenis.javalinfly.Post;
import com.github.unldenis.javalinfly.Put;
import com.github.unldenis.javalinfly.Query;
import com.github.unldenis.javalinfly.Response;
import com.github.unldenis.javalinfly.openapi.OpenApiTranslator;
import com.github.unldenis.javalinfly.processor.Round;
import com.github.unldenis.javalinfly.processor.utils.ProcessorUtil;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ControllersRound extends Round {
  private final MessagerRound messager;
  private final RoundEnvironment roundEnv;
  private final TypeMirror rolesTypeMirror;



  public final OpenApiTranslator openApiTranslator = new OpenApiTranslator();

  public Map<String, ExecutableElement> selectedRoles = new HashMap<>();

  // generator
  public final Set<String> endpoints = new HashSet<>();
  public final Set<String> handlersField = new HashSet<>();


  public ControllersRound(MessagerRound messager, RoundEnvironment roundEnv,
      TypeMirror rolesTypeMirror) {
    this.messager = messager;
    this.roundEnv = roundEnv;
    this.rolesTypeMirror = rolesTypeMirror;
  }


  @Override
  protected void run() {
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
        if (get != null) {
          handlerType = "GET";
          responseType = get.responseType().compiled();
          handlerRoles = get.roles();
          summary = get.summary();
        } else if (post != null) {
          handlerType = "POST";
          responseType = post.responseType().compiled();
          handlerRoles = post.roles();
          summary = post.summary();
        } else if (put != null) {
          handlerType = "PUT";
          responseType = put.responseType().compiled();
          handlerRoles = put.roles();
          summary = put.summary();
        } else if (delete != null) {
          handlerType = "DELETE";
          responseType = delete.responseType().compiled();
          handlerRoles = delete.roles();
          summary = delete.summary();
        } else {
          return;
        }

        String returnType = executableElement.getReturnType().toString();
        if (executableElement.getReturnType().getKind() != TypeKind.DECLARED
            || !returnType.startsWith(Response.class.getName())) {
          messager.error(executableElement, "Endpoint method must return a Response");
          return ;
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
        for (VariableElement variableElement : executableElement.getParameters()) {

          String nameParameter = variableElement.getSimpleName().toString();
          String typeParameter = variableElement.asType().toString();
          String classParameter = typeParameter.split("<")[0];

          Body body = variableElement.getAnnotation(Body.class);
          if (body != null) {

            parametersCall.add(nameParameter);

            if (body.customType()) {
              if (!typeParameter.equals(String.class.getName())) {
                messager.error(variableElement, "Body parameter '%s' must be a String since is customType",
                    nameParameter);
                return;
              }
              parametersDecl.add(String.format("String %s = ctx.body();\n", nameParameter));
            } else {
              parametersDecl.add(
                  String.format("%s %s = (%s) ctx.bodyAsClass(%s.class);\n", typeParameter,
                      nameParameter, typeParameter, classParameter));
            }
          }

          Path path = variableElement.getAnnotation(Path.class);
          if (path != null) {

            endpointPath.append("/{").append(nameParameter).append("}");
            parametersCall.add(nameParameter);

            parametersDecl.add(String.format("String %s = ctx.pathParam(\"%s\");\n", nameParameter,
                nameParameter));

            pathParameters.add(nameParameter);
          }

          Query query = variableElement.getAnnotation(Query.class);
          if (query != null) {

            parametersCall.add(nameParameter);

            parametersDecl.add(String.format("String %s = ctx.queryParam(\"%s\");\n", nameParameter,
                nameParameter));

            queryParameters.add(nameParameter);
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

        openApiTranslator.addPath(
            endpointPath.toString(),
            handlerType,
            handlerRoles,
            summary,
            pathParameters,
            queryParameters

        );

//        var roles = "new String[]{"+ Arrays.stream(handlerRoles).map(roleName -> "\"" + roleName + "\"").collect(Collectors.joining(",")) + "}";
//        if(handlerRoles.length == 0) {
//          roles = "new String[0]";
//        }
//
//        openApiStatements.add(
//            String.format(
//                "openApiTranslator.addPath(\"%s\", \"%s\", %s, \"%s\", %s, %s);\n",
//                endpointPath.toString(),
//                handlerType,
//                roles,
//                summary,
//                pathParameters.isEmpty() ? "Collections.emptyList()" : String.format("Arrays.asList(%s)", String.join(",", pathParameters)),
//                queryParameters.isEmpty() ? "Collections.emptyList()" : String.format("Arrays.asList(%s)", String.join(",", queryParameters))
//        ));
      }
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
