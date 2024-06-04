package com.github.unldenis.javalinfly;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Put {

  String[] roles() default {};

  ResponseType responseType() default ResponseType.JSON;

  String summary() default "";

}
