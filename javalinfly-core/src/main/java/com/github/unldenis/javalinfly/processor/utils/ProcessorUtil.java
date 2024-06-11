package com.github.unldenis.javalinfly.processor.utils;

import java.util.Map.Entry;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.jetbrains.annotations.Nullable;

import static java.util.Locale.ENGLISH;

public final class ProcessorUtil {

  private ProcessorUtil() {
  }

  public static String getPackageName(TypeElement classElement) {
    return ((PackageElement) classElement.getEnclosingElement()).getQualifiedName().toString();
  }

  public static String getSimpleNameAsString(Element element) {
    return element.getSimpleName().toString();
  }

  public static String capitalize(String name) {
    return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
  }

  public static String notCapitalize(String name) {
    return name.substring(0, 1).toLowerCase(ENGLISH) + name.substring(1);
  }


  public static boolean isTypeElement(Element element) {
    return element instanceof TypeElement;
  }

  public static TypeElement asTypeElement(Types typeUtils, TypeMirror typeMirror) {
    return (TypeElement) typeUtils.asElement(typeMirror);
  }

  public static @Nullable AnnotationMirror getAnnotationMirror(TypeElement typeElement, Class<?> clazz) {
    String clazzName = clazz.getName();
    for(AnnotationMirror m : typeElement.getAnnotationMirrors()) {
      if(m.getAnnotationType().toString().equals(clazzName)) {
        return m;
      }
    }
    return null;
  }

  public static @Nullable AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
    for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues()
        .entrySet()) {
      if (entry.getKey().getSimpleName().toString().equals(key)) {
        return entry.getValue();
      }
    }
    return null;
  }



}