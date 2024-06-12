package com.github.unldenis;

import com.github.unldenis.Bootstrap.MyRoles;
import com.github.unldenis.javalinfly.JavalinFly;
import com.github.unldenis.javalinfly.JavalinFlyInjector;
import com.github.unldenis.javalinfly.JavalinFlyInjector.Info;
import com.github.unldenis.javalinfly.JavalinFlyInjector.Info.Contact;
import io.javalin.Javalin;
import io.javalin.security.RouteRole;

import java.util.Map;


public class Bootstrap {

    public enum MyRoles implements RouteRole {
        GUEST,
        USER,
        ADMIN
    }


    @JavalinFlyInjector(
        rolesClass = MyRoles.class,
        info = @Info(
            title = "My App",
            contact = @Contact(
                url = "qlsol.com"
            )
        )
    )
    public static void main(String[] args) {

        Javalin app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);


        JavalinFly.inject(app, config -> {
            config.pathPrefix = "/api/v1";
        });
    }

    // https://stackoverflow.com/questions/38926255/maven-annotation-processing-processor-not-found
}
