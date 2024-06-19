package com.github.unldenis.javalinfly;

import io.javalin.http.Context;

public class ContextExt {

  public static void fileResponse(Context context, FileResponse fileResponse) {
    context.contentType("application/octet-stream");
    context.header("Content-Disposition", "attachment; filename=" + fileResponse.nameFile());
    context.result(fileResponse.data());
  }

}
