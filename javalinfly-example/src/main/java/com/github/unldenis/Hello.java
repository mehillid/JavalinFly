package com.github.unldenis;

import com.github.unldenis.javalinfly.*;
import com.github.unldenis.javalinfly.annotation.Body;
import com.github.unldenis.javalinfly.annotation.Controller;
import com.github.unldenis.javalinfly.annotation.Get;
import com.github.unldenis.javalinfly.annotation.Post;
import io.javalin.http.Context;

@Controller(path = "/hello")
public class Hello {

    public Hello() {}

    @Get(responseType = ResponseType.STRING, roles = {"USER", "ADMIN"})
    public SuccessResponse<String> main(Context ctx) {
        return SuccessResponse.ok();
    }


    @Post(responseType = ResponseType.STRING)
    public Response<String, String> createAll(Context ctx, @Body(customType = true) String users) {
        return Response.ok("posted all");
    }

}