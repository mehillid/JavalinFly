package com.github.unldenis

import com.github.unldenis.javalinfly.*
import com.github.unldenis.javalinfly.kotlin.ok
import io.javalin.http.Context

@Controller(path = "/hello")
class Hello {
    @Get(responseType = ResponseType.STRING, roles = ["user", "admin"])
    fun main(ctx: Context): Response<String, String> {
        return ok("hello")
    }

    @Post(responseType = ResponseType.STRING)
    fun createAll(ctx: Context, @Body(customType = true) users: String): Response<String, String> {
        return ok("posted all")
    }
}