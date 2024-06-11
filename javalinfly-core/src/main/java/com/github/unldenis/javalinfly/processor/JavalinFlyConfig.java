package com.github.unldenis.javalinfly.processor;

import com.github.unldenis.javalinfly.openapi.model.Info;
import com.github.unldenis.javalinfly.openapi.model.Info.Contact;
import io.javalin.security.RouteRole;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JavalinFlyConfig {

//    public Map<String, ? extends RouteRole> roles;
    public String pathPrefix = "";

    public Info openapi = new Info(
        "My App",
        "0.1",
        new Contact(
            "API Support",
            "https://www.wikipedia.com/",
            "admin@domain.com"
        )
    );

}
