package com.github.unldenis.javalinfly;

import com.github.unldenis.javalinfly.openapi.model.OpenApi;
import com.github.unldenis.javalinfly.openapi.model.Servers;
import java.util.List;

public class Vars {


  private static String OPEN_API_SPEC = null;

  public static String openApiSpec() {
    return OPEN_API_SPEC;
  }

  public static void openApiSpec(String openApiSpec) {
    OPEN_API_SPEC = openApiSpec;
  }


  private static String SWAGGER_UI = null;

  public static String swaggerUi() {
    return SWAGGER_UI;
  }

  public static void swaggerUi(String swaggerUi) {
    SWAGGER_UI = swaggerUi;
  }
}
