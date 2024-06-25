package com.quicklink.javalinfly.processor.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jetbrains.annotations.NotNull;

public class JsonUtils {

  private static JsonUtils instance;
  public static @NotNull JsonUtils get() {
    if(instance == null) {
      instance = new JsonUtils();
    }
    return instance;
  }

  private final ObjectMapper MAPPER = new ObjectMapper();

  public JsonUtils() {
    MAPPER.setSerializationInclusion(Include.NON_NULL);
    MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
  }

  public String serialize(Object value) {
    try {
      return MAPPER.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> T deserialize(String content, TypeReference<T> valueTypeRef) {
    try {
      return MAPPER.readValue(content, valueTypeRef);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }


}
