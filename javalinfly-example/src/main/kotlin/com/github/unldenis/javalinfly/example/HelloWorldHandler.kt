package com.github.unldenis.javalinfly.example

import com.github.unldenis.javalinfly.Controller
import com.github.unldenis.javalinfly.Get
import com.github.unldenis.javalinfly.Response
import io.javalin.http.Context

@Controller(path = "/helloworld")
class HelloWorldHandler {
    inner class Person(var name: String, var age: Int)

    @Get
    fun helloPerson(ctx: Context): Response<Person, String> {
        return Response.ok(Person("Joe", 25))
    }
}
