package com.github.unldenis;

import com.github.unldenis.javalinfly.JavalinFly;
import com.github.unldenis.javalinfly.JavalinFlyInjector;
import io.javalin.Javalin;
import io.javalin.security.RouteRole;

import java.util.Map;

@JavalinFlyInjector(roles = {"guest", "user", "admin"})
public class Bootstrap {

    public enum MyRoles implements RouteRole {
        GUEST,
        USER,
        ADMIN
    }

    public static void main(String[] args) {

        Javalin app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);


        JavalinFly.inject(app, config -> {
            config.roles = Map.of("guest", MyRoles.GUEST, "user", MyRoles.USER, "admin", MyRoles.ADMIN);
            config.pathPrefix = "/api/v1";
        });
    }

    // https://stackoverflow.com/questions/38926255/maven-annotation-processing-processor-not-found
}
