package com.quicklink.javalinfly.processor.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Messager {

  private static javax.annotation.processing.Messager messager;

  private static File file;

  private static boolean logs = false;

  public static void enable() {
    logs = true;

    file = new File("debug_compiler.txt");
    try {
      if(!file.exists()) {
        file.createNewFile();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void set(javax.annotation.processing.Messager messager) {
    Messager.messager = messager;
  }

  private static void writeString(@NotNull String prefix, @Nullable Element e, @NotNull String msg, @NotNull Object... args) {
    try {
      Files.writeString(file.toPath(), String.format("%s (at %s) - %s", prefix, e, String.format(msg, args)), StandardOpenOption.APPEND);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static void error(@NotNull Element e, @NotNull String msg, @NotNull Object... args) {
    if(!logs) {
      return;
    }

    writeString("ERROR", e, msg, args);
    messager.printMessage(Kind.ERROR, String.format(msg, args), e);

//    throw new RuntimeException(String.format(msg, args));
  }

  public static void error(@NotNull String msg, @NotNull Object... args) {
    if(!logs) {
      return;
    }

    writeString("WARNING", null, msg, args);
    messager.printMessage(Kind.ERROR, String.format(msg, args));

  }

  public static void print(@NotNull String msg, @NotNull Object... args) {
    if(!logs) {
      return;
    }

    messager.printMessage(Kind.NOTE, String.format(msg, args));
  }

  public static void warning(@NotNull String msg, @NotNull Object... args) {
    if(!logs) {
      return;
    }

    writeString("WARNING", null, msg, args);
    messager.printMessage(Kind.WARNING, String.format(msg, args));
  }

  public static void warning(@NotNull Element e, @NotNull String msg, @NotNull Object... args) {
    if(!logs) {
      return;
    }

    messager.printMessage(Kind.WARNING, String.format(msg, args), e);
  }
}
