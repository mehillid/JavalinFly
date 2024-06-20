package com.quicklink.javalinfly.exception.handler.impl;

import com.quicklink.javalinfly.exception.handler.JavalinFlyHandlerException;

public class QueryParamNotFoundException extends JavalinFlyHandlerException {

  public QueryParamNotFoundException(String nameParameter) {
    super(String.format("Missing query parameter '%s'", nameParameter));
  }

}
