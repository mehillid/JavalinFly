package com.github.unldenis.javalinfly;

import com.github.unldenis.javalinfly.processor.JavalinFlyProcessor;
import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;

import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;

public class JavalinFly {

  public static void inject(Javalin app) {
    try {
      Class<?> cl = Class.forName(JavalinFlyProcessor.FULL_CLASS);
      cl.getDeclaredMethod(JavalinFlyProcessor.METHOD_NAME).invoke(cl.newInstance());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(String.format("class %s not injected", JavalinFlyProcessor.FULL_CLASS));
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

}
