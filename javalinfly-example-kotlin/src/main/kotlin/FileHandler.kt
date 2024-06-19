package com.github.unldenis

import com.github.unldenis.javalinfly.*
import com.github.unldenis.javalinfly.annotation.Body
import com.github.unldenis.javalinfly.annotation.Controller
import com.github.unldenis.javalinfly.annotation.Get
import com.github.unldenis.javalinfly.annotation.Post
import com.github.unldenis.javalinfly.kotlin.response
import com.github.unldenis.javalinfly.kotlin.successResponse
import io.javalin.http.Context
import io.javalin.http.UploadedFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Controller(path = "/file")
class FileHandler {

    @Get(responseType = ResponseType.FILE)
    fun getTestFile(ctx : Context) = response<FileResponse, UserHandler.StandardError> {
        ok = FileResponse.of("test.txt", "Hello World".encodeToByteArray())
    }


    @Post(responseType = ResponseType.STRING)
    fun addFile(ctx: Context, @Body file : UploadedFile) = successResponse<String> {
        val fileName = file.filename()

        val targetFile = Paths.get("clone.txt")

        val contentFileInput = file.content()
        Files.copy(contentFileInput, targetFile, StandardCopyOption.REPLACE_EXISTING)
        contentFileInput.close()
    }
}