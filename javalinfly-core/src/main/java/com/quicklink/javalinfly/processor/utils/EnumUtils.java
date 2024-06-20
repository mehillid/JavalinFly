package com.quicklink.javalinfly.processor.utils;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;

public class EnumUtils {
    private final ProcessingEnvironment processingEnv;

    public EnumUtils(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    public Set<String> getEnumConstants(TypeMirror typeMirror) {
        Types typeUtils = processingEnv.getTypeUtils();

        // Check if the TypeMirror represents a declared type (class or interface)
        if (!(typeMirror instanceof DeclaredType)) {
            throw new IllegalArgumentException("TypeMirror is not a declared type");
        }

        // Get the TypeElement for the TypeMirror
        TypeElement typeElement = (TypeElement) typeUtils.asElement(typeMirror);
        if (typeElement.getKind() != ElementKind.ENUM) {
            throw new IllegalArgumentException("TypeMirror does not represent an enum type");
        }

        // Retrieve the enum constants
        Set<String> enumConstants = new HashSet<>();
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.ENUM_CONSTANT) {
                enumConstants.add(enclosedElement.getSimpleName().toString());
            }
        }

        return enumConstants;
    }
}
