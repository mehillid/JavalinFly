package com.quicklink.javalinfly.processor.utils;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import org.jetbrains.annotations.NotNull;

public class Messager {

  private static javax.annotation.processing.Messager messager;

  public static void set(javax.annotation.processing.Messager messager) {
    Messager.messager = messager;
  }


  public static void error(@NotNull Element e, @NotNull String msg, @NotNull Object... args) {
    messager.printMessage(Kind.ERROR, String.format(msg, args), e);
  }

  public static void error(@NotNull String msg, @NotNull Object... args) {
    messager.printMessage(Kind.ERROR, String.format(msg, args));
  }

  public static void print(@NotNull String msg, @NotNull Object... args) {
    messager.printMessage(Kind.NOTE, String.format(msg, args));
  }

  public static void warning(@NotNull String msg, @NotNull Object... args) {
    messager.printMessage(Kind.WARNING, String.format(msg, args));
  }

  public static void warning(@NotNull Element e, @NotNull String msg, @NotNull Object... args) {
    messager.printMessage(Kind.WARNING, String.format(msg, args), e);
  }
}
