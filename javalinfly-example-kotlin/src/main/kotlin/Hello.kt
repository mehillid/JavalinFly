package com.quicklink.javalinfly.kotlin.example

import com.quicklink.javalinfly.annotation.Body
import com.quicklink.javalinfly.annotation.Controller
import com.quicklink.javalinfly.annotation.Get
import com.quicklink.javalinfly.annotation.Post
import com.quicklink.javalinfly.kotlin.response
import com.quicklink.javalinfly.kotlin.successResponse
import com.quicklink.javalinfly.ResponseType
import io.javalin.http.Context

@Controller(path = "/hello")
class Hello {
    @Get(responseType = ResponseType.STRING, roles = ["USER", "ADMIN"])
    fun main(ctx: Context) = successResponse<String> {

        // this is success since there is no err
    }

    @Post(responseType = ResponseType.STRING)
    fun createAll(ctx: Context, @Body(customType = true) users: String) = response<String, String> {
        err = "posted all"
    }
}