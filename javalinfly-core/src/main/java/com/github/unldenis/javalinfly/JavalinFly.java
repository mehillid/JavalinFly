package com.github.unldenis.javalinfly;

import com.github.unldenis.javalinfly.processor.JavalinFlyConfig;
import com.github.unldenis.javalinfly.processor.JavalinFlyProcessor;
import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;

import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class JavalinFly {

  public static void inject(@NotNull Supplier<JavalinFlyConfig> config) {
    try {
      Class<?> cl = Class.forName(JavalinFlyProcessor.FULL_CLASS);
      Object instance = cl.getDeclaredConstructor().newInstance();
      cl.getDeclaredMethod(JavalinFlyProcessor.METHOD_NAME, Supplier.class).invoke(instance, config);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(String.format("class %s not injected", JavalinFlyProcessor.FULL_CLASS));
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

}
