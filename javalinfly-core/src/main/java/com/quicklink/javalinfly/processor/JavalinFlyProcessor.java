package com.quicklink.javalinfly.processor;

import com.quicklink.javalinfly.annotation.JavalinFlyInjector;
import com.quicklink.javalinfly.processor.round.ControllersRound;
import com.quicklink.javalinfly.processor.round.GeneratorRound;
import com.quicklink.javalinfly.processor.round.JavalinFlyInjectorRound;
import com.quicklink.javalinfly.processor.round.MessagerRound;
import com.google.auto.service.AutoService;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

@SupportedAnnotationTypes({"com.quicklink.javalinfly.annotation.JavalinFlyInjector",
    "com.quicklink.javalinfly.annotation.Controller"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class JavalinFlyProcessor extends AbstractProcessor {


  private MessagerRound messagerRound;
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
    messagerRound = new MessagerRound(env.getMessager());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        if (roundEnv.processingOver()) {
//            return true;
//        }
    // check injector
//    messagerRound.print("** Hello");

    // javalinflyinjector
    if(javalinFlyInjectorRound == null) {
      javalinFlyInjectorRound = new JavalinFlyInjectorRound(messagerRound, roundEnv, processingEnv);
    }
    javalinFlyInjectorRound.execute();

    if(javalinFlyInjectorRound.rolesTypeMirror == null) {
      return true;
    }

    if(controllersRound == null) {
      controllersRound = new ControllersRound(typeUtils, elementUtils, messagerRound, roundEnv,
          javalinFlyInjectorRound.rolesTypeMirror, javalinFlyInjectorRound.javalinFlyInjectorAnn);
    }
    controllersRound.execute();


    if(controllersRound.endpoints.isEmpty()) {
//      messagerRound.error("Missing a class annotated with @%s", Controller.class.getSimpleName());
      return true;
    }

    if(!javalinFlyInjectorRound.executed() && controllersRound.executed()) {
      messagerRound.error("Missing a class annotated with @%s", JavalinFlyInjector.class.getSimpleName());
      return true;
    }

    if(generatorRound == null) {
      generatorRound = new GeneratorRound(filer, messagerRound, javalinFlyInjectorRound, controllersRound);
    }

    generatorRound.execute();




    return false;
  }







}
