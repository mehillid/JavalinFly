package com.quicklink.javalinfly.processor;

import com.quicklink.javalinfly.openapi.model.OpenApi;
import org.jetbrains.annotations.NotNull;

public class JavalinFlyConfig {

//    public Map<String, ? extends RouteRole> roles;
    public String pathPrefix = "";

    public OpenApiFun openapi = openApi -> {};

    public interface OpenApiFun {
        void edit(@NotNull OpenApi openApi);
    }
}
