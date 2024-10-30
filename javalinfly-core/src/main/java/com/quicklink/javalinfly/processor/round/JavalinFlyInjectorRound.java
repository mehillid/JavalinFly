package com.quicklink.javalinfly.processor.round;

import com.quicklink.javalinfly.annotation.JavalinFlyInjector;
import com.quicklink.javalinfly.processor.Round;
import com.quicklink.javalinfly.processor.utils.EnumUtils;
import com.quicklink.javalinfly.processor.utils.Messager;
import com.quicklink.javalinfly.processor.utils.ProcessorUtil;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public class JavalinFlyInjectorRound extends Round {

  public record Input(ProcessingEnvironment processingEnv, Set<? extends Element> injectors) {}

  private final Input input;

  public TypeMirror rolesTypeMirror;
  public Set<String> injectorRoles;
  public Element injectorElement;
  public JavalinFlyInjector javalinFlyInjectorAnn;

  public JavalinFlyInjectorRound(Input input) {
    this.input = input;
  }


  @Override
  protected void run() {
    if (!input.injectors().isEmpty()) {

      if (input.injectors().size() > 1) {
        Messager.error("More than a class annotated with @%s", JavalinFlyInjector.class.getSimpleName());
      }

      injectorElement = input.injectors().iterator().next();

      // Check if a class has been annotated with @JavalinFlyInjector
      if (injectorElement.getKind() != ElementKind.METHOD) {
        Messager.error(injectorElement, "Only methods can be annotated with @%s",
            JavalinFlyInjector.class.getSimpleName());
        return ;
      }

      ExecutableElement annotatedElement = (ExecutableElement) injectorElement;
      AnnotationMirror annotationMirror = ProcessorUtil.getAnnotationMirror(annotatedElement,
          JavalinFlyInjector.class);

      if (annotationMirror == null) {
        Messager.error(injectorElement,
            "Error loading AnnotationMirror from type element %s of annotation @%s",
            annotatedElement.getSimpleName().toString(),
            JavalinFlyInjector.class.getSimpleName());
        return;
      }

      AnnotationValue annotationValue = ProcessorUtil.getAnnotationValue(annotationMirror,
          "rolesClass");
      if (annotationValue == null) {
        Messager.error(injectorElement,
            "Error loading AnnotationValue from type element %s of annotation @%s",
            annotatedElement.getSimpleName().toString(),
            JavalinFlyInjector.class.getSimpleName());
        return ;
      }
      rolesTypeMirror = (TypeMirror) annotationValue.getValue();

      boolean implementsInterface = ProcessorUtil.implementsInterface(input.processingEnv(),
          rolesTypeMirror, "io.javalin.security.RouteRole");

      if (!implementsInterface) {
        Messager.error(injectorElement, "Class '%s' does not implement '%s'", rolesTypeMirror.toString(),
            "io.javalin.security.RouteRole");
        return;
      }
      injectorRoles = new EnumUtils(input.processingEnv()).getEnumConstants(rolesTypeMirror);


      javalinFlyInjectorAnn = annotatedElement.getAnnotation(JavalinFlyInjector.class);

      if(javalinFlyInjectorAnn.logs()) {
        Messager.enable();
      }

    }
  }

  @Override
  public boolean executed() {
    return super.executed() && injectorRoles != null && javalinFlyInjectorAnn != null;
  }
}
