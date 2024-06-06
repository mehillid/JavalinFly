package com.github.unldenis.javalinfly.processor;

import io.javalin.Javalin;
import io.javalin.security.RouteRole;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JavalinFlyConfig {

    final Javalin app;
    public Map<String, ? extends RouteRole> roles = null;


    public JavalinFlyConfig(@NotNull Javalin app) {
        this.app = app;
    }
}
