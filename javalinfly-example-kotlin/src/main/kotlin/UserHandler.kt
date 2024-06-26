package com.quicklink.javalinfly.kotlin.example

import com.quicklink.javalinfly.ResponseType
import com.quicklink.javalinfly.annotation.*
import com.quicklink.javalinfly.kotlin.response
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
    @Get(tags = ["user"])
    fun getAll(ctx: Context) = response<List<User>, StandardError> {
        ok = emptyList()

    }

    @Put
    fun putUser(ctx : Context, @Query userId : String, @Body user : User) = response<User, StandardError> {
        ok = user
    }
    class Users {
        var users: LinkedHashMap<String, User>? = null
    }

    class User {
        lateinit var name: String
        var age: Int = 0

        constructor()

        constructor(name: String, age: Int) {
            this.name = name
            this.age = age
        }
    }


    class StandardError(var cause: String, var details : String?)
}

