package com.github.unldenis.javalinfly.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class InterfaceChecker {
    private final ProcessingEnvironment processingEnv;

    public InterfaceChecker(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    public boolean implementsInterface(TypeMirror typeMirror, String interfaceClass) {
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
}
