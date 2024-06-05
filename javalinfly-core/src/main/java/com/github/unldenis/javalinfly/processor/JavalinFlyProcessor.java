package com.github.unldenis.javalinfly.processor;

import com.github.unldenis.javalinfly.Controller;
import com.github.unldenis.javalinfly.JavalinFly;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;

public class JavalinFlyProcessor extends AbstractProcessor {

    public static String SIMPLE_CLASS_NAME = "GeneratedClass";
    public static String PACKAGE_NAME  = JavalinFlyProcessor.class.getName() ;
    public static String FULL_CLASS = PACKAGE_NAME + "." + SIMPLE_CLASS_NAME;


    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

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


        Set<? extends Element> controllers =  roundEnv.getElementsAnnotatedWith(Controller.class);

        // Iterate over all @Controller annotated elements
        for (Element annotatedElement : controllers) {


            // Check if a class has been annotated with @Controller
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                error(annotatedElement, "Only classes can be annotated with @%s", Controller.class.getSimpleName());
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

                // logic
            }
        }


        if(controllers.size() > 0) {
//        String packageName = elementUtils.getPackageOf(annotatedElement).getQualifiedName().toString();

            generateClass(controllers.iterator().next());

        }


        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Controller.class.getName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void generateClass(Element element) {
        String source = "package " + PACKAGE_NAME + ";\n\n" +
                "public class " + SIMPLE_CLASS_NAME + " {\n" +
                "    public " + PACKAGE_NAME + "(){}\n" +
                "    public void hello() {\n" +
                "        System.out.println(\"Hello from \" + getClass().getSimpleName());\n" +
                "    }\n" +
                "}\n";

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
