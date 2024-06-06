package com.github.unldenis;


import com.github.unldenis.javalinfly.Controller;
import com.github.unldenis.javalinfly.Get;
import com.github.unldenis.javalinfly.Post;
import com.github.unldenis.javalinfly.Response;
import io.javalin.http.Context;

@Controller(path = "/user") public class User {

    @Post
    public Response<String, String> createUser(Context ctx) {
        return Response.err("this is my create error");
    }

    @Get
    public Response<String, String> getUser(Context ctx) {
        return Response.ok("user joe, age 26");
    }
}

