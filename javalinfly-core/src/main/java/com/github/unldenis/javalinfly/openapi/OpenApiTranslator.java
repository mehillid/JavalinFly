package com.github.unldenis.javalinfly.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class OpenApiTranslator {

  private final ObjectMapper MAPPER;

  public OpenApiTranslator() {
    MAPPER = new ObjectMapper();
    MAPPER.setSerializationInclusion(Include.NON_NULL);
    MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

  }



}
