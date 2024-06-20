package com.github.unldenis.javalinfly.exception.handler.impl;

import com.github.unldenis.javalinfly.annotation.Query;
import com.github.unldenis.javalinfly.exception.handler.JavalinFlyHandlerException;

public class QueryParamNotFoundException extends JavalinFlyHandlerException {

  public QueryParamNotFoundException(String nameParameter) {
    super(String.format("Missing query parameter '%s'", nameParameter));
  }

}
