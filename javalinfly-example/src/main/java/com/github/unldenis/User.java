package com.github.unldenis;


import com.github.unldenis.javalinfly.*;
import io.javalin.http.Context;

@Controller(path = "/user") public class User {

    @Post(responseType = ResponseType.STRING)
    public Response<String, String> createUser(Context ctx) {
        return Response.err("this is my create error");
    }

    @Get(responseType = ResponseType.STRING)
    public Response<String, String> getUser(Context ctx) {
        return Response.ok("user joe, age 26");
    }
}

