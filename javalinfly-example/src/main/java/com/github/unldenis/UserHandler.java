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
    public Response<Users, StandardError> createAll(Context ctx, @Body Users users) {
        return Response.ok(users);
    }

    @Get(responseType = ResponseType.STRING)
    public Response<String, String> getUser(Context ctx) {
        return Response.ok("user joe, age 26");
    }

    public static class Users {
        public List<User> users;


        public Users() {}
        public Users(List<User> users) {
            this.users = users;
        }
    }

    public static class User {
        public String name;
        public int age;

        public User() {}

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

