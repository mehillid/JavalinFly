package com.quicklink.javalinfly.openapi;


import com.goterl.resourceloader.FileLoader;
import com.quicklink.javalinfly.JavalinFly;

import com.quicklink.javalinfly.processor.utils.ResourceUtil;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class SwaggerUIHtmlGenerator {



    public static String generateSwaggerUIHtml(String openApiJson) {
        String swaggerUiCss = ResourceUtil.readResourceFile("swagger-ui.css");
        String swaggerUiBundleJs =  ResourceUtil.readResourceFile(
            "swagger-ui-bundle.js");
        String swaggerUiStandalonePresetJs =  ResourceUtil.readResourceFile(
            "swagger-ui-standalone-preset.js");

        StringBuilder swaggerUiTemplate = new StringBuilder()
                .append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\">\n")
                .append("<head>\n")
                .append("    <meta charset=\"UTF-8\">\n")
                .append("    <title>Swagger UI</title>\n")
                .append("    <style>\n")
                .append("    * {\n")
                .append("        margin: 0;\n")
                .append("        padding: 0;\n")
                .append("    }\n")
                .append(swaggerUiCss)
                .append("    </style>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <div id=\"swagger-ui\"></div>\n")
                .append("    <script>\n")
                .append(swaggerUiBundleJs)
                .append("    </script>\n")
                .append("    <script>\n")
                .append(swaggerUiStandalonePresetJs)
                .append("    </script>\n")
                .append("    <script>\n")
                .append("    window.onload = function() {\n")
                .append("        const ui = SwaggerUIBundle({\n")
                .append("            spec: ").append(openApiJson).append(",\n")
                .append("            dom_id: '#swagger-ui',\n")
                .append("            presets: [\n")
                .append("                SwaggerUIBundle.presets.apis,\n")
                .append("                SwaggerUIStandalonePreset\n")
                .append("            ],\n")
                .append("            layout: \"StandaloneLayout\"\n")
                .append("        });\n")
                .append("    }\n")
                .append("    </script>\n")
                .append("</body>\n")
                .append("</html>\n");

        return swaggerUiTemplate.toString();
    }
}
