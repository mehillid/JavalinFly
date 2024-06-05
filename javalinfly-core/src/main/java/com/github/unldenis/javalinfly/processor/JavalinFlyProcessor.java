package com.github.unldenis.javalinfly.processor;


import com.github.unldenis.javalinfly.Controller;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class JavalinFlyProcessor extends AbstractProcessor {
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
      super.init(processingEnv);
      typeUtils = processingEnv.getTypeUtils();
      elementUtils = processingEnv.getElementUtils();
      filer = processingEnv.getFiler();
      messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annoations, RoundEnvironment env) {
      // Itearate over all @Controller annotated elements
      for (Element annotatedElement : env.getElementsAnnotatedWith(Controller.class)) {

        // Check if a class has been annotated with @Factory
        if (annotatedElement.getKind() != ElementKind.CLASS) {
          error(annotatedElement, "Only classes can be annotated with @%s", Controller.class.getSimpleName());
          return true;
        }

        for(Element method : annotatedElement.getEnclosedElements()) {
          if(method.getKind() != ElementKind.METHOD) {
            continue;
          }
          if(!method.getModifiers().contains(Modifier.PUBLIC)) {
            continue;
          }


          ExecutableElement executableElement = (ExecutableElement) method;

        }
      }
      return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
      Set<String> annotataions = new LinkedHashSet<>();
      annotataions.add(Controller.class.getName());
      return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latestSupported();
    }

    private void error(Element e, String msg, Object... args) {
      messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }



}