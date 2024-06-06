package com.github.unldenis;

import com.github.unldenis.javalinfly.JavalinFly;
import com.github.unldenis.javalinfly.processor.JavalinFlyConfig;
import io.javalin.Javalin;
import io.javalin.http.HandlerType;
import io.javalin.security.RouteRole;

import java.util.Map;

public class Bootrstrap {

    public enum MyRoles implements RouteRole {
        GUEST,
        USER,
        ADMIN
    }

    public static void main(String[] args) {

        Javalin app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);
        JavalinFlyConfig config = new JavalinFlyConfig(app);
        config.roles = Map.of("guest", MyRoles.GUEST, "user", MyRoles.USER, "admin", MyRoles.ADMIN);
        JavalinFly.inject(() -> config);
    }

    // https://stackoverflow.com/questions/38926255/maven-annotation-processing-processor-not-found
}
