package com.quicklink.javalinfly.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface JavalinFlyInjector {
  Class<? extends Enum> rolesClass();

  String[] defaultRoles() default {};

  boolean generateDocumentation() default true;

  boolean logs() default false;


//  Info info() default @Info();
//
//  @interface Info {
//    String title() default "App built with JavalinFly";
//    String version() default "0.1";
//    Contact contact() default @Contact();
//
//    @interface Contact {
//      String name() default "API Support";
//      String url() default "https://www.wikipedia.com/";
//      String email() default "admin@domain.com";
//    }
//  }
}
