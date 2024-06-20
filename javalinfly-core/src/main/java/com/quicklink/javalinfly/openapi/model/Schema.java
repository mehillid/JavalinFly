package com.quicklink.javalinfly.openapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class Schema {

  public static String schemaRef(String name) {
    return "#/components/schemas/" + name;
  }

  public String $ref;
  public String type;
  public Map<String, Schema> properties;
  public List<String> required;
  public Schema items;
  @JsonProperty("enum")
  public List<String> _enum;
  public String description;
  public String format;
  @JsonProperty("default")
  public Object _default;
  public Integer minimum;
  public Integer maximum;
  public Boolean exclusiveMinimum;
  public Boolean exclusiveMaximum;
  public Integer minLength;
  public Integer maxLength;
  public String pattern;
  public String title;
  public Schema additionalProperties;
  public Object example;


  public static Builder builder() {
    return new Builder();
  }

  // Static inner Builder class
  public static class Builder {

    private Builder() {}

    private String $ref;
    private String type;
    private Map<String, Schema> properties;
    private List<String> required;
    private Schema items;
    private List<String> _enum;
    private String description;
    private String format;
    private Object _default;
    private Integer minimum;
    private Integer maximum;
    private Boolean exclusiveMinimum;
    private Boolean exclusiveMaximum;
    private Integer minLength;
    private Integer maxLength;
    private String pattern;
    private String title;
    private Schema additionalProperties;
    private Object example;

    public Builder $ref(String $ref) {
      this.$ref = $ref;
      return this;
    }

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public Builder properties(Map<String, Schema> properties) {
      this.properties = properties;
      return this;
    }

    public Builder required(List<String> required) {
      this.required = required;
      return this;
    }

    public Builder items(Schema items) {
      this.items = items;
      return this;
    }

    public Builder _enum(List<String> _enum) {
      this._enum = _enum;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder format(String format) {
      this.format = format;
      return this;
    }

    public Builder _default(Object _default) {
      this._default = _default;
      return this;
    }

    public Builder minimum(Integer minimum) {
      this.minimum = minimum;
      return this;
    }

    public Builder maximum(Integer maximum) {
      this.maximum = maximum;
      return this;
    }

    public Builder exclusiveMinimum(Boolean exclusiveMinimum) {
      this.exclusiveMinimum = exclusiveMinimum;
      return this;
    }

    public Builder exclusiveMaximum(Boolean exclusiveMaximum) {
      this.exclusiveMaximum = exclusiveMaximum;
      return this;
    }

    public Builder minLength(Integer minLength) {
      this.minLength = minLength;
      return this;
    }

    public Builder maxLength(Integer maxLength) {
      this.maxLength = maxLength;
      return this;
    }

    public Builder pattern(String pattern) {
      this.pattern = pattern;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder additionalProperties(Schema additionalProperties) {
      this.additionalProperties = additionalProperties;
      return this;
    }

    public Builder example(Object example) {
      this.example = example;
      return this;
    }

    public Schema build() {
      Schema schema = new Schema();
      schema.$ref = this.$ref;
      schema.type = this.type;
      schema.properties = this.properties;
      schema.required = this.required;
      schema.items = this.items;
      schema._enum = this._enum;
      schema.description = this.description;
      schema.format = this.format;
      schema._default = this._default;
      schema.minimum = this.minimum;
      schema.maximum = this.maximum;
      schema.exclusiveMinimum = this.exclusiveMinimum;
      schema.exclusiveMaximum = this.exclusiveMaximum;
      schema.minLength = this.minLength;
      schema.maxLength = this.maxLength;
      schema.pattern = this.pattern;
      schema.title = this.title;
      schema.additionalProperties = this.additionalProperties;
      schema.example = this.example;
      return schema;
    }
  }
}
