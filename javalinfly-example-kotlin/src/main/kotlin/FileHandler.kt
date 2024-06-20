package com.quicklink.javalinfly.kotlin.example

import com.quicklink.javalinfly.annotation.Body
import com.quicklink.javalinfly.annotation.Controller
import com.quicklink.javalinfly.annotation.Get
import com.quicklink.javalinfly.annotation.Post
import com.quicklink.javalinfly.kotlin.response
import com.quicklink.javalinfly.kotlin.successResponse

import com.quicklink.javalinfly.FileResponse
import com.quicklink.javalinfly.ResponseType
import io.javalin.http.Context
import io.javalin.http.UploadedFile
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