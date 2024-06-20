package com.quicklink.javalinfly.openapi.model;

public class Tag {

  public final String name;

  public String description;
  public ExternalDocs externalDocs;

  public Tag(String name) {
    this.name = name;
  }

  public static class ExternalDocs {
    public final String description;
    public final String url;

    public ExternalDocs(String description, String url) {
      this.description = description;
      this.url = url;
    }
  }
}
