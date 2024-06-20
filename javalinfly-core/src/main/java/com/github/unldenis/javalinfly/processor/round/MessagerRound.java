package com.github.unldenis.javalinfly.processor.round;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import org.jetbrains.annotations.NotNull;

public class MessagerRound {

  private final Messager messager;

  public MessagerRound(Messager messager) {
    this.messager = messager;
  }

  public void error(@NotNull Element e, @NotNull String msg, @NotNull Object... args) {
    messager.printMessage(Kind.ERROR, String.format(msg, args), e);
  }

  public void error(@NotNull String msg, @NotNull Object... args) {
    messager.printMessage(Kind.ERROR, String.format(msg, args));
  }

  public void print(@NotNull String msg, @NotNull Object... args) {
    messager.printMessage(Kind.NOTE, String.format(msg, args));
  }

  public void warning(@NotNull String msg, @NotNull Object... args) {
    messager.printMessage(Kind.WARNING, String.format(msg, args));
  }

  public void warning(@NotNull Element e, @NotNull String msg, @NotNull Object... args) {
    messager.printMessage(Kind.WARNING, String.format(msg, args), e);
  }
}
