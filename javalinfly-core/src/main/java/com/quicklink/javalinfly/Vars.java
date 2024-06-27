package com.quicklink.javalinfly;

import com.quicklink.javalinfly.openapi.SwaggerUIHtmlGenerator;
public class Vars {

  public static final String RESOURCE_FILE_SPEC = "openapiSpec.json";


  public static String openApiSpec() {
    return SwaggerUIHtmlGenerator.readResourceFile(RESOURCE_FILE_SPEC);
  }

  private static String SWAGGER_UI = null;

  public static String swaggerUi() {
    return SWAGGER_UI;
  }

  public static void swaggerUi(String swaggerUi) {
    SWAGGER_UI = swaggerUi;
  }
}
