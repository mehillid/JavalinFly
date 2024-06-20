package com.quicklink.javalinfly.annotation;


import com.quicklink.javalinfly.ResponseType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Post {

  String[] roles() default {};

  ResponseType responseType() default ResponseType.JSON;

  String summary() default "";

  String[] tags() default {};

}
