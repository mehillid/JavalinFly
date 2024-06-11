package com.github.unldenis
import com.github.unldenis.javalinfly.JavalinFlyInjector

import io.javalin.security.RouteRole

@JavalinFlyInjector(roles = ["guest", "user", "admin"])
object Bootstrap

enum class MyRoles : RouteRole {
    GUEST,
    USER,
    ADMIN
}