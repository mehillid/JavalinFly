package com.github.unldenis

import com.github.unldenis.javalinfly.JavalinFly
import com.github.unldenis.javalinfly.JavalinFlyInjector
import io.javalin.Javalin
import java.util.*

@JavalinFlyInjector(rolesClass = MyRoles::class)
object Bootstrap

fun main() {
    val app = Javalin.create()
        .get("/") { ctx -> ctx.result("Hello World") }
        .start(7070)


    JavalinFly.inject(app) {

        it.pathPrefix = "/api/v1"
    }
} // https://stackoverflow.com/questions/38926255/maven-annotation-processing-processor-not-found