package com.quicklink.javalinfly.openapi.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class OpenApi {

  public final String openapi;
  public Info info;
  public List<Servers> servers;
  public final List<Security> security;
  public final Components components;
  public final LinkedHashMap<String, Path> paths;
  public final Collection<Tag> tags;

  public OpenApi(String openapi, Info info, List<Servers> servers, List<Security> security,
      Components components, LinkedHashMap<String, Path> paths, Collection<Tag> tags) {
    this.openapi = openapi;
    this.info = info;
    this.servers = servers;
    this.security = security;
    this.components = components;
    this.paths = paths;
    this.tags = tags;
  }

  public static Builder builder() {
    return new Builder();
  }

  // Static inner Builder class
  public static class Builder {

    private Builder() {}

    private String openapi;
    private Info info;
    private List<Servers> servers;
    private List<Security> security;
    private Components components;
    private LinkedHashMap<String, Path> paths;
    private Collection<Tag> tags;

    public Builder openapi(String openapi) {
      this.openapi = openapi;
      return this;
    }

    public Builder info(Info info) {
      this.info = info;
      return this;
    }

    public Builder servers(List<Servers> servers) {
      this.servers = servers;
      return this;
    }

    public Builder security(List<Security> security) {
      this.security = security;
      return this;
    }

    public Builder components(Components components) {
      this.components = components;
      return this;
    }

    public Builder paths(LinkedHashMap<String, Path> paths) {
      this.paths = paths;
      return this;
    }

    public Builder tags(Collection<Tag> tags) {
      this.tags = tags;
      return this;
    }

    public OpenApi build() {
      return new OpenApi(openapi, info, servers, security, components, paths, tags);
    }
  }
}
