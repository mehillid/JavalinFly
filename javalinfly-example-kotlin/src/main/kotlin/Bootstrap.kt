package com.github.unldenis

import io.javalin.security.RouteRole



enum class MyRoles : RouteRole {
    GUEST,
    USER,
    ADMIN
}