package com.quicklink.javalinfly.openapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.List;

public class Path {

  public PathMethod get;
  public PathMethod post;
  public PathMethod put;
  public PathMethod delete;

  public static class PathMethod {
    public List<String> tags;
    public String summary;
    public String description;
    public RequestBody requestBody;
    public LinkedHashMap<String, Response> responses;
    public List<Parameter> parameters;

    public static class RequestBody {
      public final Content content;
      public String description;
      public Boolean required;

      public RequestBody(Content content) {
        this.content = content;
      }
    }

    public static class Response {
      public final String description;
      public Content content;

      public Response(String description) {
        this.description = description;
      }
    }


    public static class Parameter {
      public final String name;
      public final Schema schema;
      public String in = "path";
      public String description;
      public Boolean required;

      public Parameter(String name, Schema schema) {
        this.name = name;
        this.schema = schema;
      }
    }

  }

  public static class Content {
    @JsonProperty("application/json")
    public ContentJson applicationJson;

    @JsonProperty("application/octet-stream")
    public ContentFile applicationFile;

    @JsonProperty("multipart/form-data")
    public ContentFile multipartFormData;


    public static class ContentJson {
      public final Schema schema;

      public ContentJson(Schema schema) {
        this.schema = schema;
      }
    }

    public static class ContentFile {
      public final Schema schema = Schema.builder()
          .type("string")
          .format("binary")
          .build();
    }
  }
}
