package com.quicklink.javalinfly.example;

import com.quicklink.javalinfly.Response;
import com.quicklink.javalinfly.ResponseType;
import com.quicklink.javalinfly.SuccessResponse;
import com.quicklink.javalinfly.annotation.Body;
import com.quicklink.javalinfly.annotation.Controller;
import com.quicklink.javalinfly.annotation.Get;
import com.quicklink.javalinfly.annotation.Post;
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