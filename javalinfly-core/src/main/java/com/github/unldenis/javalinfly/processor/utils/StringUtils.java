package com.github.unldenis.javalinfly.processor.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {

  public static String arrayToJavaCode(String[] arr) {
    if(arr.length == 0) {
      return  "new String[0]";
    } else {
      return "new String[]{"+ Arrays.stream(arr).map(roleName -> "\"" + roleName + "\"").collect(
          Collectors.joining(",")) + "}";
    }

  }

}
