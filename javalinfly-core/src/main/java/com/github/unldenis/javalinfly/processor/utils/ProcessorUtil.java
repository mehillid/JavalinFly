package com.github.unldenis.javalinfly.processor.utils;

import java.util.List;
import java.util.Map.Entry;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import org.jetbrains.annotations.NotNull;
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

  public static @Nullable AnnotationMirror getAnnotationMirror(Element element, Class<?> clazz) {
    String clazzName = clazz.getName();
    for(AnnotationMirror m : element.getAnnotationMirrors()) {
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

  public static boolean implementsInterface(ProcessingEnvironment processingEnv, TypeMirror typeMirror, String interfaceClass) {
    Types typeUtils = processingEnv.getTypeUtils();
    Elements elementUtils = processingEnv.getElementUtils();

    // Get the TypeElement for the interface
    TypeElement interfaceElement = elementUtils.getTypeElement(interfaceClass);
    if (interfaceElement == null) {
//            throw new IllegalArgumentException("Interface not found: " + interfaceClass.getCanonicalName());
      return false;
    }

    TypeMirror interfaceTypeMirror = interfaceElement.asType();

    // Check if the TypeMirror implements the interface
    return typeUtils.isAssignable(typeMirror, interfaceTypeMirror);
  }

  public static List<? extends TypeMirror> getGenericTypes(TypeMirror typeMirror) {
    DeclaredType declaredType = (DeclaredType) typeMirror;
    return declaredType.getTypeArguments();
  }

  public TypeElement getExtracted(Types types, VariableElement ve) {
    TypeMirror typeMirror = ve.asType();
    Element element = types.asElement(typeMirror);

    // instanceof implies null-ckeck
    return (element instanceof TypeElement)
        ? (TypeElement)element : null;
  }

  public static String getClassNameWithoutAnnotations(TypeMirror typeMirror) {
    // Check if the type is declared (i.e., a class or interface type)
    if (typeMirror.getKind() == TypeKind.DECLARED) {
      DeclaredType declaredType = (DeclaredType) typeMirror;
      TypeElement typeElement = (TypeElement) declaredType.asElement();
      return typeElement.getQualifiedName().toString();
    } else {
      return typeMirror.toString();
    }
  }
}