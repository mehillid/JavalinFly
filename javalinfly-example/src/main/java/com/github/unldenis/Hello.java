package com.github.unldenis;

import com.github.unldenis.javalinfly.*;
import io.javalin.http.Context;

@Controller(path = "/hello")
public class Hello {

    public Hello() {}

    @Get(responseType = ResponseType.STRING, roles = {"user", "admin"})
    public Response<String, String> main(Context ctx) {

        return Response.ok("hello");
    }

    @Post(responseType = ResponseType.STRING)
    public Response<String, String> createAll(Context ctx, @Body(customType = true) String users) {
        return Response.ok("posted all");
    }

}