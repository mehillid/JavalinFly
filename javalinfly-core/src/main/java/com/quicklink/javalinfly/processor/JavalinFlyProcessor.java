package com.quicklink.javalinfly.processor;

import com.quicklink.javalinfly.annotation.JavalinFlyInjector;
import com.quicklink.javalinfly.processor.round.ControllersRound;
import com.quicklink.javalinfly.processor.round.GeneratorRound;
import com.quicklink.javalinfly.processor.round.JavalinFlyInjectorRound;
import com.quicklink.javalinfly.processor.round.JavalinFlyInjectorRound.Input;
import com.quicklink.javalinfly.processor.utils.Messager;
import com.google.auto.service.AutoService;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

@SupportedAnnotationTypes({"com.quicklink.javalinfly.annotation.JavalinFlyInjector",
    "com.quicklink.javalinfly.annotation.Controller"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class JavalinFlyProcessor extends AbstractProcessor {

  private JavalinFlyInjectorRound javalinFlyInjectorRound;
  private ControllersRound controllersRound;
  private GeneratorRound generatorRound;

  private Types typeUtils;
  private Elements elementUtils;
  private Filer filer;


  @Override
  public synchronized void init(ProcessingEnvironment env) {
    super.init(env);
    typeUtils = env.getTypeUtils();
    elementUtils = env.getElementUtils();
    filer = env.getFiler();
    Messager.set(env.getMessager());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for(final TypeElement annotation : annotations) {
      final Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(annotation);

      if(annotation.getQualifiedName().toString().equals(JavalinFlyInjector.class.getName())) {
        javalinFlyInjectorRound = new JavalinFlyInjectorRound(new Input(processingEnv, annotated));
      } else {

        // controller
        controllersRound = new ControllersRound(new ControllersRound.Input(typeUtils, elementUtils, annotated));
      }

    }



    if(roundEnv.processingOver()) {
      javalinFlyInjectorRound.execute();
      if(!javalinFlyInjectorRound.executed()) {
        Messager.error("injectorRound not executed");
        return true;
      }
      controllersRound.injectorRound = javalinFlyInjectorRound;
      controllersRound.execute();
      if(!controllersRound.executed()) {
        Messager.error("controllersRound not executed");
        return true;
      }
      generatorRound = new GeneratorRound(new GeneratorRound.Input(filer, javalinFlyInjectorRound, controllersRound));
      generatorRound.execute();

    }


    return false;
  }







}
