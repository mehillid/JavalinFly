package com.github.unldenis

import com.github.unldenis.javalinfly.JavalinFly
import io.javalin.Javalin
import io.javalin.http.Context

fun main() {
    val app = Javalin.create()
        .get("/") { ctx: Context -> ctx.result("Hello World") }
        .start(7070)


    JavalinFly.inject(app) {
        it.roles = mapOf(
            "guest" to MyRoles.GUEST,
            "user" to MyRoles.USER,
            "admin" to MyRoles.ADMIN
        )
        it.pathPrefix = "/api/v1"
    }
} // https://stackoverflow.com/questions/38926255/maven-annotation-processing-processor-not-found