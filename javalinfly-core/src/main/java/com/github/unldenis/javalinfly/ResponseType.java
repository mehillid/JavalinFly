package com.github.unldenis.javalinfly;

public enum ResponseType {

  JSON(
          "if(response.isErr()) { ctx.status(404).json(response.unwrapErr()); }else{ ctx.json(response.unwrap()); }"
  ),
  HTML(
          "if(response.isErr()) { ctx.html(response.unwrapErr()); }else{ ctx.html(response.unwrap()); }"),
  FILE(
          ""
  ),
  STRING(
          "if(response.isErr()) { ctx.status(404).result((String) response.unwrapErr()); }else{ ctx.result(response.unwrap()); }"
  );

  private final String compiled;


  ResponseType(String compiled) {
    this.compiled = compiled;
  }

  public String compiled() {
    return compiled;
  }
}
