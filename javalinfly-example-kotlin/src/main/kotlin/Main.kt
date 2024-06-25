package com.quicklink.javalinfly.kotlin.example

import com.quicklink.javalinfly.JavalinFly
import com.quicklink.javalinfly.annotation.JavalinFlyInjector
import com.quicklink.javalinfly.kotlin.openapi
import com.quicklink.javalinfly.openapi.model.Servers
import io.javalin.Javalin
import io.javalin.security.RouteRole

enum class MyRoles : RouteRole {
    GUEST,
    USER,
    ADMIN
}



@JavalinFlyInjector(
    rolesClass = MyRoles::class,
    generateDocumentation = true
)
fun main() {
    val app = Javalin.create()
        .get("/") { ctx -> ctx.result("Hello World") }
        .start(7070)


    JavalinFly.inject(app) {

        it.pathPrefix = "/api/v1"
        it.openapi {
            info.title = "My App"
            servers = listOf(
                Servers(
                    "http://localhost:7070/api/v1",
                    "This is localhost server"
                ),
                Servers(
                    "https://domain.com/api/v1",
                    "Cloud server"
                )
            )
        }
    }
} // https://stackoverflow.com/questions/38926255/maven-annotation-processing-processor-not-found