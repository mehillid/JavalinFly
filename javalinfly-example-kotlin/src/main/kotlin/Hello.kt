package com.github.unldenis

import com.github.unldenis.javalinfly.*
import com.github.unldenis.javalinfly.kotlin.response
import com.github.unldenis.javalinfly.kotlin.successResponse
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