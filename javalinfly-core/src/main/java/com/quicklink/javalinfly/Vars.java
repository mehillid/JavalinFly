package com.quicklink.javalinfly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Vars {

  public static final String RESOURCE_FILE_SPEC = "openapi-spec.json";


  public static String openApiSpec() {
    try {
      var path = Paths.get(
          Objects.requireNonNull(Vars.class.getClassLoader().getResource(RESOURCE_FILE_SPEC)).toURI());

      var content = Files.readString(path);

      return content;

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }



  private static String SWAGGER_UI = null;

  public static String swaggerUi() {
    return SWAGGER_UI;
  }

  public static void swaggerUi(String swaggerUi) {
    SWAGGER_UI = swaggerUi;
  }
}