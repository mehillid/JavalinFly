package com.quicklink.javalinfly.processor.utils;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.stream.Collectors;

public class ResourceUtil {

  public static String readResourceFile(String fileName) {
    try (InputStream inputStream = ResourceUtil.class.getResourceAsStream(
        "/" + fileName)) {
      if(inputStream == null) {
        throw new FileNotFoundException("Resource '%s' not found!".formatted(fileName));
      }
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(inputStream))) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
