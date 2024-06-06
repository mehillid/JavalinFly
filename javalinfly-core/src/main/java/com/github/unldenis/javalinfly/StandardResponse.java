package com.github.unldenis.javalinfly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StandardResponse<T> extends Response<T,  String> {

    public final String exception;

    public StandardResponse(T value, String error, String exception) {
        super(value, error);
        this.exception = exception;
    }

    @Override
    public @Nullable StandardError unwrapErr() {
        return new StandardError((String) super.unwrapErr(), exception);
    }

    public static class StandardError {
        public String cause;
        public String exception;

        public StandardError(@NotNull String cause, @Nullable String exception) {
            this.cause = cause;
            this.exception = exception;
        }
    }
}
