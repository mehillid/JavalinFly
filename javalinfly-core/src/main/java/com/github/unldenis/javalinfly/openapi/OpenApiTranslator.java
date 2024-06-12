package com.github.unldenis.javalinfly.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.unldenis.javalinfly.openapi.model.Components;
import com.github.unldenis.javalinfly.openapi.model.Components.SecuritySchemes;
import com.github.unldenis.javalinfly.openapi.model.Components.SecuritySchemes.BearerAuth;
import com.github.unldenis.javalinfly.openapi.model.Info;
import com.github.unldenis.javalinfly.openapi.model.Info.Contact;
import com.github.unldenis.javalinfly.openapi.model.OpenApi;
import com.github.unldenis.javalinfly.openapi.model.Path;
import com.github.unldenis.javalinfly.openapi.model.Path.PathMethod;
import com.github.unldenis.javalinfly.openapi.model.Path.PathMethod.Parameter;
import com.github.unldenis.javalinfly.openapi.model.Schema;
import com.github.unldenis.javalinfly.openapi.model.Security;
import com.github.unldenis.javalinfly.openapi.model.Servers;
import com.github.unldenis.javalinfly.openapi.model.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public class OpenApiTranslator {

  private final ObjectMapper MAPPER;
  private final LinkedHashMap<String, Path> path_mapped = new LinkedHashMap<>();
  private final LinkedHashMap<String, Tag> tags = new LinkedHashMap<>();

  public OpenApiTranslator() {
    MAPPER = new ObjectMapper();
    MAPPER.setSerializationInclusion(Include.NON_NULL);
    MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

  }

  public void addPath(String path, String method, String[] roles, String summary,
      List<String> pathParameters, List<String> queryParameters,  String[] pathTags) {

    // ** start tags
    for(String tag : pathTags) {
      if(!tags.containsKey(tag)) {
        Tag tagModel = new Tag(tag);
        tags.put(tag, tagModel);
      }
    }
    // ** end tags


    Path cachedPath = path_mapped.getOrDefault(path, new Path());
    PathMethod cachedPathMethod = new Path.PathMethod();

    // description
    String description = roles.length == 0  ? null : "Limited to " + String.join(" or ", roles);
    // end description

    cachedPathMethod.summary = summary;
    cachedPathMethod.description = description;
    cachedPathMethod.responses = new LinkedHashMap<>();
    cachedPathMethod.tags = Arrays.stream(pathTags).collect(Collectors.toList());

    int parametersAmount = 0;
    for (String pathParam : pathParameters) {
      if(parametersAmount == 0) {
        cachedPathMethod.parameters = new ArrayList<>();
        parametersAmount++;
      }

      Parameter parameter = new Parameter(pathParam,Schema.builder().type("string").build());
      parameter.in = "path";
      parameter.required = true;
      cachedPathMethod.parameters.add(parameter);
    }

    for (String queryParam : queryParameters) {
      if(parametersAmount == 0) {
        cachedPathMethod.parameters = new ArrayList<>();
        parametersAmount++;
      }

      Parameter parameter = new Parameter(queryParam,Schema.builder().type("string").build());
      parameter.in = "query";
      cachedPathMethod.parameters.add(parameter);
    }

    switch (method) {
      case "GET": {
        cachedPath.get = cachedPathMethod;
        break;
      }
      case "POST": {
        cachedPath.post = cachedPathMethod;
        break;
      }
      case "PUT": {
        cachedPath.put = cachedPathMethod;
        break;
      }
      case "DELETE": {
        cachedPath.delete = cachedPathMethod;
        break;
      }
      default: {
        throw new IllegalStateException(String.format("invalid method %s of path %s", method, path));
      }
    }

    path_mapped.put(path, cachedPath);


  }


  public OpenApi build()  {
    return OpenApi.builder()
        .openapi("3.0.3")
        .info(new Info(
            "App built with JavalinFly",
            "0.1",
            new Contact(
                "Denis",
                "github.com/unldenis",
                "user@domain.com"
            ))
        )
        .servers(Collections.emptyList())
        .security(List.of(new Security(Collections.emptyList())))
        .components(new Components(
            new SecuritySchemes(
                new BearerAuth("http", "bearer", "JWT")
            ),
            Map.of()
        ))
        .paths(path_mapped)
        .tags(tags.values())
        .build();

  }

  public String asString(OpenApi openApi) {
    try {
      return MAPPER.writeValueAsString(openApi);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }


}
