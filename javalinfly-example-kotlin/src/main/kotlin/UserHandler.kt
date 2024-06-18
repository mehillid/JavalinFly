package com.github.unldenis

import com.github.unldenis.javalinfly.*
import com.github.unldenis.javalinfly.kotlin.response
import io.javalin.http.Context

@Controller(path = "/user")
class UserHandler {

    @Post(tags = ["user"])
    fun createAll(ctx: Context, @Body users: Users) = response<Users, StandardError> {
        ok = Users()
    }

    @Get(responseType = ResponseType.STRING, tags = ["user"])
    fun getUser(ctx: Context, @Path id: String, @Query age: String?) = response<String, String> {

        if (age == null)
            err = "age is missing"

        ok = "id $id, age $age"

    }

    class Users {
        var users: List<User>? = null


        constructor()
        constructor(users: List<User>?) {
            this.users = users
        }
    }

    class User {
        var name: String? = null
        var age: Int = 0

        constructor()

        constructor(name: String?, age: Int) {
            this.name = name
            this.age = age
        }
    }


    class StandardError(var cause: String)
}

