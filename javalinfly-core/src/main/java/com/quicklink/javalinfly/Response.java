package com.quicklink.javalinfly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Response<T, E> {

  public static @NotNull<T, E> Response<T, E> ok(T value) {
    return new Response<>(value, null);
  }

  public static @NotNull<T, E> Response<T, E> err(E error) {
    return new Response<>(null, error);
  }


  private final T value;
  private final E error;


  Response(T value, E error) {
    this.value = value;
    this.error = error;
  }

  public boolean isOk() {
    return value != null;
  }

  public boolean isErr() {
    return error != null;
  }

  public @Nullable T unwrap() {
    return value;
  }


  public @Nullable Object unwrapErr() {
    return error;
  }
}
