package com.github.unldenis.javalinfly;

import com.github.unldenis.javalinfly.openapi.model.OpenApi;
import com.github.unldenis.javalinfly.openapi.model.Servers;
import java.util.List;

public class Vars {


  private static List<Servers> SERVERS;

  public static List<Servers> servers() {
    return SERVERS;
  }

  public static void servers(List<Servers> servers) {
    SERVERS = servers;
  }


  private static OpenApi OPEN_API;

  public static OpenApi openApi() {
    return OPEN_API;
  }

  public static void openApi(OpenApi openApi) {
    OPEN_API = openApi;
  }

}
