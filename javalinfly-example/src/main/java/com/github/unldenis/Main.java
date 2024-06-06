package com.github.unldenis;

import com.github.unldenis.javalinfly.Controller;
import com.github.unldenis.javalinfly.Get;
import com.github.unldenis.javalinfly.JavalinFly;
import com.github.unldenis.javalinfly.Response;
import io.javalin.http.Context;

@Controller(path = "/hello")
public class Main {

    @Get(roles = {"user", "admin"})
    public Response<String, String> main(Context ctx) {
        ctx.result("Hello world!");

        return Response.ok("hello");
    }


}