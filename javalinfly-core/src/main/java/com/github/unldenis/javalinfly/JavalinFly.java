package com.github.unldenis.javalinfly;

import com.github.unldenis.javalinfly.processor.JavalinFlyConfig;
import com.github.unldenis.javalinfly.processor.JavalinFlyProcessor;
import com.github.unldenis.javalinfly.processor.round.GeneratorRound;
import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;

import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JavalinFly {

  public static void inject(@NotNull Javalin app, @NotNull Consumer<JavalinFlyConfig> config) {
    try {
      Class<?> cl = Class.forName(GeneratorRound.FULL_CLASS);
      Object instance = cl.getDeclaredConstructor().newInstance();
      cl.getDeclaredMethod(GeneratorRound.METHOD_NAME, Javalin.class, Consumer.class).invoke(instance, app, config);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(String.format("class %s not injected", GeneratorRound.FULL_CLASS));
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

}
