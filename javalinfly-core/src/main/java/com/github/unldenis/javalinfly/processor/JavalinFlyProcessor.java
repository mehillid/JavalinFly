package com.github.unldenis.javalinfly.processor;



import com.github.unldenis.javalinfly.Controller;
import com.github.unldenis.javalinfly.JavalinFly;
import io.javalin.Javalin;
import java.util.HashSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JavalinFlyProcessor extends AbstractProcessor {


  private PrintStream outputStream;

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(Controller.class);
    for (Element element : annotated) {
      if (!(element instanceof TypeElement)) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only classes and records can be annotated with @Controller", element);
        continue;
      }

      TypeElement typeElement = (TypeElement) element;

      if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Elements annotated with @Controller cannot be abstract", element);
        continue;
      }



      boolean result = this.registerControllerClass(typeElement);
      if (result) {
        continue;
      }

      this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Elements annotated with @Controller must provide a public no parameters constructor or only a Whatsapp instance", element);
    }

    if (roundEnv.processingOver()) {
      this.closeOutputFile();
    }

    return true;
  }


  private boolean registerControllerClass(TypeElement classElement) {
    for (Element member : classElement.getEnclosedElements()) {
      if (!(member instanceof ExecutableElement)) {
        continue;
      }

      ExecutableElement executableElement = (ExecutableElement) member;

      if (!executableElement.getModifiers().contains(Modifier.PUBLIC) || executableElement.getKind() != ElementKind.CONSTRUCTOR) {
        continue;
      }

      if (executableElement.getParameters().isEmpty()) {
        this.createOutputFile(classElement);
        this.outputStream.printf("            whatsapp.addListener(new %s());%n", classElement.getQualifiedName());
        return true;
      }

      if (executableElement.getParameters().size() == 1 && this.isJavalinType(executableElement)) {
        this.createOutputFile(classElement);
        this.outputStream.printf("            whatsapp.addListener(new %s(whatsapp));%n", classElement.getQualifiedName());
        return true;
      }
    }

    return false;
  }

  private boolean isJavalinType(ExecutableElement executableElement) {
    VariableElement parameterType = executableElement.getParameters().get(0);
    return parameterType instanceof TypeElement
        && ((TypeElement) parameterType).getQualifiedName().contentEquals(JAVALIN_TYPE);
  }


  private void createOutputFile(Element owner) {
    if (this.outputStream == null) {
      try {
        JavaFileObject resource = this.processingEnv.getFiler().createSourceFile(QUALIFIED_CLASS_NAME, owner);
        this.outputStream = new PrintStream(resource.openOutputStream());
        this.outputStream.printf("package %s;%n", PACKAGE_NAME);
        this.outputStream.printf("public class %s {%n", CLASS_NAME);
        this.outputStream.printf("    public static void %s(%s javalin) {%n", METHOD_NAME, JAVALIN_TYPE);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  private void closeOutputFile() {
    this.outputStream.println("   }");
    this.outputStream.println("}");
    this.outputStream.close();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new HashSet<>();
    types.add(Controller.class.getName());
    return types;
  }

  public static String qualifiedClassName() {
    return QUALIFIED_CLASS_NAME;
  }

  public static String methodName() {
    return METHOD_NAME;
  }


  private static final String CLASS_NAME = "JavalinFlyBootstrap";
  private static final String PACKAGE_NAME = JavalinFly.class.getPackage().getName();
  private static final String QUALIFIED_CLASS_NAME = PACKAGE_NAME + "." + CLASS_NAME;
  private static final String METHOD_NAME = "register";
  private static final String JAVALIN_TYPE = Javalin.class.getName();
}