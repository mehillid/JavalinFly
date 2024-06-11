package com.github.unldenis.javalinfly.openapi.model;

import java.util.List;

public class Security {

  public final List<String> bearerAuth;

  public Security(List<String> bearerAuth) {
    this.bearerAuth = bearerAuth;
  }
}
