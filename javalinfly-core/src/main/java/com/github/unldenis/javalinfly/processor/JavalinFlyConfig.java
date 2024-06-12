package com.github.unldenis.javalinfly.processor;

import com.github.unldenis.javalinfly.openapi.model.Info;
import com.github.unldenis.javalinfly.openapi.model.Info.Contact;
import com.github.unldenis.javalinfly.openapi.model.Servers;
import io.javalin.security.RouteRole;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JavalinFlyConfig {

//    public Map<String, ? extends RouteRole> roles;
    public String pathPrefix = "";

    public List<Servers> openapiServers = Collections.emptyList();

}
