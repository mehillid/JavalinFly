package com.github.unldenis.javalinfly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SuccessResponse<E> {


  public static @NotNull<E> SuccessResponse<E> ok() {
    return new SuccessResponse<>("{}", null);
  }

  public static @NotNull<E> SuccessResponse<E> err(E error) {
    return new SuccessResponse<>(null, error);
  }


  private final String value;
  private final E error;


  SuccessResponse(String value, E error) {
    this.value = value;
    this.error = error;
  }

  public boolean isOk() {
    return value != null;
  }

  public boolean isErr() {
    return error != null;
  }

  public @Nullable Object unwrap() {
    return value;
  }

  public @Nullable Object unwrapErr() {
    return error;
  }
}
