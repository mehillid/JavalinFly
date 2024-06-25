package com.quicklink.javalinfly.example;

import com.quicklink.javalinfly.JavalinFly;
import com.quicklink.javalinfly.annotation.JavalinFlyInjector;
import io.javalin.Javalin;
import io.javalin.security.RouteRole;


public class Bootstrap {

    public enum MyRoles implements RouteRole {
        GUEST,
        USER,
        ADMIN
    }


    @JavalinFlyInjector(
        rolesClass = MyRoles.class,
        defaultRoles = {"USER", "ADMIN"},
        generateDocumentation = true
    )
    public static void main(String[] args) {

        Javalin app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);


        JavalinFly.inject(app, config -> {
            config.pathPrefix = "/api/v1";
            config.openapi = openApi -> {
              openApi.info.version = "1.0";
            };
        });
    }

    // https://stackoverflow.com/questions/38926255/maven-annotation-processing-processor-not-found
}
