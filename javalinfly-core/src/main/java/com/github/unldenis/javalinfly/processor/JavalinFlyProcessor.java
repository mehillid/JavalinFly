package com.github.unldenis.javalinfly.processor;

import com.github.unldenis.javalinfly.*;
import com.google.auto.service.AutoService;
import io.javalin.Javalin;
import io.javalin.http.HandlerType;
import io.javalin.router.Endpoint;
import io.javalin.router.EndpointMetadata;
import io.javalin.security.Roles;
import io.javalin.security.RouteRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.*;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;

@SupportedAnnotationTypes("com.github.unldenis.javalinfly.Controller")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class JavalinFlyProcessor extends AbstractProcessor {

    public static String SIMPLE_CLASS_NAME = "GeneratedClass";
    public static String PACKAGE_NAME = JavalinFlyProcessor.class.getPackageName();
    public static String FULL_CLASS = PACKAGE_NAME + "." + SIMPLE_CLASS_NAME;
    public static String METHOD_NAME = "init";

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;


    public Set<String> imports = new HashSet<>();
    public Set<String> handlersField = new HashSet<>();
    public Set<String> handlersInit = new HashSet<>();
    public Set<String> endpoints = new HashSet<>();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        typeUtils = env.getTypeUtils();
        elementUtils = env.getElementUtils();
        filer = env.getFiler();
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Nome della nuova classe e pacchetto (modificare secondo necessità)


        print("Hello from annotation :)");
        Set<? extends Element> controllers = roundEnv.getElementsAnnotatedWith(Controller.class);

        // Iterate over all @Controller annotated elements
        for (Element elementController : controllers) {

            // Check if a class has been annotated with @Controller
            if (elementController.getKind() != ElementKind.CLASS) {
                error(elementController, "Only classes can be annotated with @%s", Controller.class.getSimpleName());
                return true;
            }


            TypeElement annotatedElement = (TypeElement) elementController;



            Controller controller = annotatedElement.getAnnotation(Controller.class);


            String varDecl = registerConstructors(annotatedElement);
            if(varDecl == null) {
                error(annotatedElement, "Class %s missing an empty constructor", annotatedElement.getQualifiedName());
                return true;
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
                if (get != null) {
                    handlerType = "GET";
                    responseType = get.responseType().compiled();
                } else if (post != null) {
                    handlerType = "POST";
                    responseType = post.responseType().compiled();
                } else if (put != null) {
                    handlerType = "PUT";
                    responseType = put.responseType().compiled();
                } else if (delete != null) {
                    handlerType = "DELETE";
                    responseType = delete.responseType().compiled();
                } else {
                    return true;
                }

                String returnType = executableElement.getReturnType().toString();
                if (executableElement.getReturnType().getKind() != TypeKind.DECLARED || !returnType.startsWith(Response.class.getName())) {
                    error(executableElement, "Endpoint method must return a Response");
                    return true;
                }


                // ** logic

                StringBuilder endpointPath = new StringBuilder(controller.path());


                List<String> parametersCall = new ArrayList<>();
                parametersCall.add("ctx");
                List<String> parametersDecl = new ArrayList<>();
                for(VariableElement variableElement : executableElement.getParameters()) {

                    String nameParameter = variableElement.getSimpleName().toString();
                    String typeParameter = variableElement.asType().toString();
                    String classParameter = typeParameter.split("<")[0];


                    Body body = variableElement.getAnnotation(Body.class);
                    if(body != null) {

                        parametersCall.add(nameParameter);

                        if(body.customType()) {
                            if(!typeParameter.equals(String.class.getName())) {
                                error(variableElement, "Body parameter '%s' must be a String since is customType", nameParameter);
                                return true;
                            }
                            parametersDecl.add(String.format("String %s = ctx.body();\n", nameParameter));
                        } else {
                            parametersDecl.add(String.format("%s %s = (%s) ctx.bodyAsClass(%s.class);\n", typeParameter, nameParameter, typeParameter, classParameter));
                        }
                    }

                    Path path = variableElement.getAnnotation(Path.class);
                    if(path != null) {

                        endpointPath.append("/{").append(nameParameter).append("}");
                        parametersCall.add(nameParameter);

                        parametersDecl.add(String.format("String %s = ctx.pathParam(\"%s\");\n", nameParameter, nameParameter));
                    }

                    Query query = variableElement.getAnnotation(Query.class);
                    if(query != null) {

                        parametersCall.add(nameParameter);

                        parametersDecl.add(String.format("String %s = ctx.queryParam(\"%s\");\n", nameParameter, nameParameter));
                    }
                }


                endpoints.add(
                        "javalin.addEndpoint(new Endpoint(HandlerType." + handlerType + ",config.pathPrefix + \""  + endpointPath.toString() + "\", config.roles.values().toArray(RouteRole[]::new), ctx -> {\n" +
                                String.join("", parametersDecl) +
                                String.format("var response = %s.%s(%s);\n", varDecl, executableElement.getSimpleName(), String.join(",", parametersCall)) +
                                responseType + "\n" +
                        "\n}));"
                );

            }
        }


        if (controllers.size() > 0) {
//        String packageName = elementUtils.getPackageOf(annotatedElement).getQualifiedName().toString();

            Element controllerElement = controllers.iterator().next();

            generateClass(controllerElement);
//            error(controllers.iterator().next(), "Error generating class %s: Testing stuff", FULL_CLASS);
//            error(e, "errore: classe %s non ha l'annotazione niagara 4", e.getSimpleName().toString());

//            return true;
        }


        return false;
    }

    private @Nullable String registerConstructors(@NotNull TypeElement classTree) {
        for (var member : classTree.getEnclosedElements()) {
            if (!(member instanceof ExecutableElement)) {
                continue;
            }
            ExecutableElement executableElement = (ExecutableElement) member;

            if (!executableElement.getModifiers().contains(Modifier.PUBLIC) || executableElement.getKind() != ElementKind.CONSTRUCTOR) {
                continue;
            }

            if (executableElement.getParameters().isEmpty()) {
                String className = classTree.getQualifiedName().toString();
                String varDecl = ProcessorUtil.notCapitalize(className.replace(".", ""));
                this.handlersField.add(String.format("private %s %s = new %s();\n", className, varDecl, className));
                return varDecl;
            }

        }

        return null;
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void error(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void print(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

    private void generateClass(Element element) {
        String source = "package " + PACKAGE_NAME + ";\n\n" +
                "import java.util.function.Consumer;\n" +
                "import java.util.HashSet;\n" +

                "import com.github.unldenis.javalinfly.processor.JavalinFlyConfig;\n" +

                "import io.javalin.Javalin;\n" +
                "import io.javalin.security.Roles;\n" +
                "import io.javalin.http.HandlerType;\n" +
                "import io.javalin.router.Endpoint;\n" +
                "import io.javalin.http.Context;\n" +
                "import io.javalin.http.Handler;\n" +
                "import io.javalin.security.RouteRole;\n" +

                "public class " + SIMPLE_CLASS_NAME + " {\n" +
                "    public " + SIMPLE_CLASS_NAME + "(){}\n" +
                String.join("", handlersField) +
                "    public void " + METHOD_NAME + "(Javalin javalin, Consumer<JavalinFlyConfig> configFun) {\n" +
                "        JavalinFlyConfig config = new JavalinFlyConfig();\n" +
                "        configFun.accept(config);\n" +
                "        System.out.println(\"Hello from \" + config.roles);\n" +
                String.join("", endpoints) +
                "    }\n" +
                "}\n";

//        print(source);
        try {
            JavaFileObject sourceFile = filer.createSourceFile(FULL_CLASS, element);
            try (Writer writer = sourceFile.openWriter()) {
                writer.write(source);
            }

        } catch (IOException e) {
            error(element, "Error generating class %s: %s", FULL_CLASS, e.getMessage());
        }
    }


}
