package com.github.unldenis

import com.github.unldenis.javalinfly.JavalinFly
import com.github.unldenis.javalinfly.JavalinFlyInjector
import com.github.unldenis.javalinfly.processor.JavalinFlyConfig
import io.javalin.Javalin
import java.util.*
import java.util.function.Consumer

@JavalinFlyInjector(
    rolesClass = MyRoles::class
)
fun main() {
    val app = Javalin.create()
        .get("/") { ctx -> ctx.result("Hello World") }
        .start(7070)


    JavalinFly.inject(app) {

        it.pathPrefix = "/api/v1"
        it.openapi = JavalinFlyConfig.OpenApiFun { openapi ->
            openapi.info.title = "My App"
        }
    }
} // https://stackoverflow.com/questions/38926255/maven-annotation-processing-processor-not-found