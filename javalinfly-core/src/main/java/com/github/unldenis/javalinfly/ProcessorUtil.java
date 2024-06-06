package com.github.unldenis.javalinfly;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

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
}