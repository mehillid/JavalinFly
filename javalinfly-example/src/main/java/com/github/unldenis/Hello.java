package com.github.unldenis;

import com.github.unldenis.javalinfly.Controller;
import com.github.unldenis.javalinfly.Get;
import com.github.unldenis.javalinfly.Response;
import com.github.unldenis.javalinfly.ResponseType;
import io.javalin.http.Context;

@Controller(path = "/hello")
public class Hello {

    public Hello() {}

    @Get(responseType = ResponseType.STRING)
    public Response<String, String> main(Context ctx) {

        return Response.ok("hello");
    }


}