package com.github.unldenis.javalinfly.annotation;


import com.github.unldenis.javalinfly.ResponseType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Delete {

  String[] roles() default {};

  ResponseType responseType() default ResponseType.JSON;

  String summary() default "";

  String[] tags() default {};

}
