package com.quicklink.javalinfly.openapi;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.quicklink.javalinfly.ResponseType;
import com.quicklink.javalinfly.openapi.model.Components;
import com.quicklink.javalinfly.openapi.model.Components.SecuritySchemes;
import com.quicklink.javalinfly.openapi.model.Components.SecuritySchemes.BearerAuth;
import com.quicklink.javalinfly.openapi.model.Info;
import com.quicklink.javalinfly.openapi.model.Info.Contact;
import com.quicklink.javalinfly.openapi.model.OpenApi;
import com.quicklink.javalinfly.openapi.model.Path;
import com.quicklink.javalinfly.openapi.model.Path.Content;
import com.quicklink.javalinfly.openapi.model.Path.Content.ContentFile;
import com.quicklink.javalinfly.openapi.model.Path.Content.ContentJson;
import com.quicklink.javalinfly.openapi.model.Path.PathMethod;
import com.quicklink.javalinfly.openapi.model.Path.PathMethod.Parameter;
import com.quicklink.javalinfly.openapi.model.Path.PathMethod.RequestBody;
import com.quicklink.javalinfly.openapi.model.Path.PathMethod.Response;
import com.quicklink.javalinfly.openapi.model.Schema;
import com.quicklink.javalinfly.openapi.model.Security;
import com.quicklink.javalinfly.openapi.model.Tag;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public class OpenApiTranslator {

  private final ObjectMapper MAPPER;
  private final LinkedHashMap<String, Path> path_mapped = new LinkedHashMap<>();
  private final LinkedHashMap<String, Tag> tags = new LinkedHashMap<>();
  private LinkedHashMap<String, Schema> schemas = new LinkedHashMap<>();

  public OpenApiTranslator() {
    MAPPER = new ObjectMapper();
    MAPPER.setSerializationInclusion(Include.NON_NULL);
    MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

  }

  public void decodeSchemas(String schemasEncoded) {
    try {
      TypeReference<LinkedHashMap<String, Schema>> typeRef = new TypeReference<>() {
      };

      schemas = MAPPER.readValue(new String(Base64.getDecoder().decode(schemasEncoded)), typeRef);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void addPath(String path, String method, String[] roles, String summary,
      List<String> pathParameters, List<String> queryParameters, String[] pathTags,
      @Nullable String bodySchema, @Nullable String responseOkSchema,
      @Nullable String responseErrSchema, ResponseType responseType) {

    // ** start tags
    for (String tag : pathTags) {
      if (!tags.containsKey(tag)) {
        Tag tagModel = new Tag(tag);
        tags.put(tag, tagModel);
      }
    }
    // ** end tags

    Path cachedPath = path_mapped.getOrDefault(path, new Path());
    PathMethod cachedPathMethod = new Path.PathMethod();

    // description
    String description = roles.length == 0 ? null : "Limited to " + String.join(" or ", roles);
    // end description

    cachedPathMethod.summary = summary;
    cachedPathMethod.description = description;
    cachedPathMethod.responses = new LinkedHashMap<>();
    cachedPathMethod.tags = Arrays.stream(pathTags).collect(Collectors.toList());

    int parametersAmount = 0;
    for (String pathParam : pathParameters) {
      if (parametersAmount == 0) {
        cachedPathMethod.parameters = new ArrayList<>();
        parametersAmount++;
      }

      Parameter parameter = new Parameter(pathParam, Schema.builder().type("string").build());
      parameter.in = "path";
      parameter.required = true;
      cachedPathMethod.parameters.add(parameter);
    }

    for (String queryParam : queryParameters) {
      if (parametersAmount == 0) {
        cachedPathMethod.parameters = new ArrayList<>();
        parametersAmount++;
      }

      Parameter parameter = new Parameter(queryParam, Schema.builder().type("string").build());
      parameter.in = "query";
      cachedPathMethod.parameters.add(parameter);
    }

    if (bodySchema != null) {
      cachedPathMethod.requestBody = new RequestBody(new Content());
      if(bodySchema.equals("$UploadedFile")) {
        cachedPathMethod.requestBody.content.multipartFormData = new ContentFile();
      } else {
        cachedPathMethod.requestBody.content.applicationJson = new ContentJson(
            schemas.get(bodySchema));
      }

    }

    switch (responseType) {
      case JSON:
        if (responseOkSchema != null) {
          var success = new Response(
              "Success response"
          );
          success.content = new Content();
          success.content.applicationJson = new ContentJson(schemas.get(responseOkSchema));

          cachedPathMethod.responses.put("200", success);
        }

        if (responseErrSchema != null) {
          var success = new Response(
              "Error response"
          );
          success.content = new Content();
          success.content.applicationJson = new ContentJson(schemas.get(responseErrSchema));

          cachedPathMethod.responses.put("400", success);
        }
        break;
      case HTML:
        break;
      case FILE:
        if (responseOkSchema != null) {
          var success = new Response(
              "Success response"
          );
          success.content = new Content();
          success.content.multipartFormData = new ContentFile();

          cachedPathMethod.responses.put("200", success);
        }
        if (responseErrSchema != null) {
          var success = new Response(
              "Error response"
          );
          success.content = new Content();
          success.content.applicationJson = new ContentJson(schemas.get(responseErrSchema));

          cachedPathMethod.responses.put("400", success);
        }
        break;
      case STRING:

        {
          var success = new Response(
              "Success response"
          );
          success.content = new Content();
          success.content.applicationJson = new ContentJson(Schema.builder().type("string").build());

          cachedPathMethod.responses.put("200", success);
        }

        {
          var success = new Response(
              "Error response"
          );
          success.content = new Content();
          success.content.applicationJson = new ContentJson(Schema.builder().type("string").build());

          cachedPathMethod.responses.put("400", success);
        }
        break;
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
        throw new IllegalStateException(
            String.format("invalid method %s of path %s", method, path));
      }
    }

    path_mapped.put(path, cachedPath);


  }


  public OpenApi build() {
    return OpenApi.builder()
        .openapi("3.0.3")
        .info(new Info(
            "App built with JavalinFly",
            "0.1",
            new Contact(
                "Denis",
                "https://github.com/unldenis",
                "user@domain.com"
            ))
        )
        .servers(Collections.emptyList())
        .security(List.of(new Security(Collections.emptyList())))
        .components(new Components(
            new SecuritySchemes(
                new BearerAuth("http", "bearer", "JWT")
            ),
            schemas
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
