package com.github.unldenis.javalinfly.processor.round;

import com.github.unldenis.javalinfly.annotation.JavalinFlyInjector;
import com.github.unldenis.javalinfly.processor.Round;
import com.github.unldenis.javalinfly.processor.utils.EnumUtils;
import com.github.unldenis.javalinfly.processor.utils.ProcessorUtil;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public class JavalinFlyInjectorRound extends Round {

  private final MessagerRound messager;
  private final RoundEnvironment roundEnv;
  private final ProcessingEnvironment processingEnv;

  public TypeMirror rolesTypeMirror;
  public Set<String> injectorRoles;
  public Element injectorElement;
  public JavalinFlyInjector javalinFlyInjectorAnn;

  public JavalinFlyInjectorRound(MessagerRound messager, RoundEnvironment roundEnv,
      ProcessingEnvironment processingEnv) {
    this.messager = messager;
    this.roundEnv = roundEnv;
    this.processingEnv = processingEnv;
  }


  @Override
  protected void run() {
    Set<? extends Element> injectors = roundEnv.getElementsAnnotatedWith(JavalinFlyInjector.class);
    if (!injectors.isEmpty()) {

      if (injectors.size() > 1) {
        messager.error("More than a class annotated with @%s", JavalinFlyInjector.class.getSimpleName());
      }

      injectorElement = injectors.iterator().next();

      // Check if a class has been annotated with @JavalinFlyInjector
      if (injectorElement.getKind() != ElementKind.METHOD) {
        messager.error(injectorElement, "Only methods can be annotated with @%s",
            JavalinFlyInjector.class.getSimpleName());
        return ;
      }

      ExecutableElement annotatedElement = (ExecutableElement) injectorElement;
      AnnotationMirror annotationMirror = ProcessorUtil.getAnnotationMirror(annotatedElement,
          JavalinFlyInjector.class);

      if (annotationMirror == null) {
        messager.error(injectorElement,
            "Error loading AnnotationMirror from type element %s of annotation @%s",
            annotatedElement.getSimpleName().toString(),
            JavalinFlyInjector.class.getSimpleName());
        return;
      }

      AnnotationValue annotationValue = ProcessorUtil.getAnnotationValue(annotationMirror,
          "rolesClass");
      if (annotationValue == null) {
        messager.error(injectorElement,
            "Error loading AnnotationValue from type element %s of annotation @%s",
            annotatedElement.getSimpleName().toString(),
            JavalinFlyInjector.class.getSimpleName());
        return ;
      }
      rolesTypeMirror = (TypeMirror) annotationValue.getValue();

      boolean implementsInterface = ProcessorUtil.implementsInterface(processingEnv,
          rolesTypeMirror, "io.javalin.security.RouteRole");

      if (!implementsInterface) {
        messager.error(injectorElement, "Class '%s' does not implement '%s'", rolesTypeMirror.toString(),
            "io.javalin.security.RouteRole");
        return;
      }
      injectorRoles = new EnumUtils(processingEnv).getEnumConstants(rolesTypeMirror);


      javalinFlyInjectorAnn = annotatedElement.getAnnotation(JavalinFlyInjector.class);

    }
  }

  @Override
  public boolean executed() {
    return super.executed() && injectorRoles != null && javalinFlyInjectorAnn != null;
  }
}
