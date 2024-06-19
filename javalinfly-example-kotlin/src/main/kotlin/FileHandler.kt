package com.github.unldenis

import com.github.unldenis.javalinfly.*
import com.github.unldenis.javalinfly.annotation.Body
import com.github.unldenis.javalinfly.annotation.Controller
import com.github.unldenis.javalinfly.annotation.Get
import com.github.unldenis.javalinfly.annotation.Post
import com.github.unldenis.javalinfly.kotlin.response
import com.github.unldenis.javalinfly.kotlin.successResponse
import io.javalin.http.Context
import java.io.File

@Controller(path = "/file")
class FileHandler {

    @Get(responseType = ResponseType.FILE)
    fun getTestFile(ctx : Context) = response<FileResponse, UserHandler.StandardError> {
        ok = FileResponse.of("test.txt", "Hello World".encodeToByteArray())
    }

}