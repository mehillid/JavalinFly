package com.quicklink.javalinfly.openapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Servers {

  public final String url;
  public final String decription;

  public Variables variables;

  public Servers(String url, String decription) {
    this.url = url;
    this.decription = decription;
  }

  public static class Variables {
    public final Port port;

    public Variables(Port port) {
      this.port = port;
    }

    public static class Port {
      @JsonProperty("enum")
      public final List<String> _enum;
      @JsonProperty("default")
      public final String _default;

      public Port(List<String> anEnum, String aDefault) {
        _enum = anEnum;
        _default = aDefault;
      }
    }
  }
}
