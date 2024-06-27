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
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Swagger UI</title>
                <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist/swagger-ui.css" />
            </head>
            <body>
            	<style>
            	* {
            		margin: 0;
            		padding: 0;
            	}
            	</style>
                <div id="swagger-ui"></div>
                        
                <script src="https://unpkg.com/swagger-ui-dist/swagger-ui-bundle.js"></script>
                <script src="https://unpkg.com/swagger-ui-dist/swagger-ui-standalone-preset.js"></script>
                <script>
                    window.onload = function() {
                        const json = %s;
                        
                        const ui = SwaggerUIBundle({
                            spec: json,
                            dom_id: '#swagger-ui',
                            presets: [
                                SwaggerUIBundle.presets.apis,
                                SwaggerUIStandalonePreset
                            ],
                            layout: "StandaloneLayout"
                        });
                        
                        window.ui = ui;
                    };
                </script>
            </body>
            </html>
            """.formatted(openApiJson);
    }
}
