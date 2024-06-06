package com.github.unldenis;


import com.github.unldenis.javalinfly.*;
import io.javalin.http.Context;

import java.util.List;

@Controller(path = "/userr") public class UserHandler {

//    @Post(responseType = ResponseType.STRING)
//    public Response<String, String> createUser(Context ctx, @Body User user) {
//
//        return Response.err("this is my create error");
//    }

    @Post()
    public Response<List<Integer>, StandardError> createAll(Context ctx, @Body List<Integer> users) {
        return Response.ok(users);
    }

    @Get(responseType = ResponseType.STRING)
    public Response<String, String> getUser(Context ctx) {
        return Response.ok("user joe, age 26");
    }

    public static class User {
        public String name;
        public int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }


    public static class StandardError {
        public String cause;

        public StandardError(String cause) {
            this.cause = cause;
        }
    }


}

