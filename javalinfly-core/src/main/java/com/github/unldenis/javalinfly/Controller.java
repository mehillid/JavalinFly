package com.github.unldenis.javalinfly;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)

public @interface Controller {

  String path();

  boolean includeApi() default true;

}
