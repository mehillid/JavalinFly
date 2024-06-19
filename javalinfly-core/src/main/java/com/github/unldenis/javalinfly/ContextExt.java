package com.github.unldenis.javalinfly;

import com.github.unldenis.javalinfly.exception.handler.impl.UploadedFileNotFoundException;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import java.util.Arrays;
import java.util.Iterator;

public class ContextExt {

  public static void fileResponse(Context context, FileResponse fileResponse) {
    context.contentType("application/octet-stream");
    context.header("Content-Disposition", "attachment; filename=" + fileResponse.nameFile());
    context.result(fileResponse.data());
  }

  public static UploadedFile uploadedFile(Context context) throws UploadedFileNotFoundException{
    Iterator<UploadedFile> iterator = context.uploadedFiles().iterator();
    if (!iterator.hasNext()) {
      throw new UploadedFileNotFoundException();
    }
    return iterator.next();
  }

}
