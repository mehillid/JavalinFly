package com.github.unldenis.javalinfly.processor.round;

import com.github.unldenis.javalinfly.JavalinFly;
import com.github.unldenis.javalinfly.JavalinFlyInjector;
import com.github.unldenis.javalinfly.openapi.model.Info;
import com.github.unldenis.javalinfly.openapi.model.Info.Contact;
import com.github.unldenis.javalinfly.processor.JavalinFlyProcessor;
import com.github.unldenis.javalinfly.processor.Round;
import io.javalin.Javalin;
import io.javalin.http.HandlerType;
import java.io.IOException;
import java.io.Writer;
import java.util.Base64;
import java.util.Collections;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;

public class GeneratorRound extends Round {
  public static String SIMPLE_CLASS_NAME = "GeneratedClass";
  public static String PACKAGE_NAME = "com.github.unldenis.javalinfly.processor.round.gen";
  public static String FULL_CLASS = PACKAGE_NAME + "." + SIMPLE_CLASS_NAME;
  public static String METHOD_NAME = "init";

  private final Filer filer;
  private final MessagerRound messager;
  private final JavalinFlyInjectorRound javalinFlyInjectorRound;
  private final ControllersRound controllersRound;

  public GeneratorRound(Filer filer, MessagerRound messager, JavalinFlyInjectorRound javalinFlyInjectorRound, ControllersRound controllersRound) {
    this.filer = filer;
    this.messager = messager;
    this.javalinFlyInjectorRound = javalinFlyInjectorRound;
    this.controllersRound = controllersRound;
  }

  @Override
  protected void run() {
//        String packageName = elementUtils.getPackageOf(annotatedElement).getQualifiedName().toString();


    for (var entry : controllersRound.selectedRoles.entrySet()) {
      if (!javalinFlyInjectorRound.injectorRoles.contains(entry.getKey())) {
        messager.error(entry.getValue(), "Role '%s' is missing at @%s", entry.getKey(),
            JavalinFlyInjector.class.getSimpleName());
        return;
      }
    }
    generateClass(javalinFlyInjectorRound.injectorElement);
//            error(controllers.iterator().next(), "Error generating class %s: Testing stuff", FULL_CLASS);
//            error(e, "errore: classe %s non ha l'annotazione niagara 4", e.getSimpleName().toString());

//            return true;
  }

  private void generateClass(Element element) {

    String source = "package " + PACKAGE_NAME + ";\n\n" +
        "import java.util.function.Consumer;\n" +
        "import java.util.HashSet;\n" +
        "import java.util.Arrays;\n" +
        "import java.util.Collections;\n" +

        "import com.github.unldenis.javalinfly.processor.JavalinFlyConfig;\n" +
        "import com.github.unldenis.javalinfly.openapi.OpenApiTranslator;\n" +
        "import com.github.unldenis.javalinfly.openapi.SwaggerUIHtmlGenerator;\n" +
        "import com.github.unldenis.javalinfly.openapi.model.OpenApi;\n" +
        "import com.github.unldenis.javalinfly.Vars;\n" +

        "import io.javalin.Javalin;\n" +
        "import io.javalin.http.HandlerType;\n" +
        "import io.javalin.http.Context;\n" +
        "import io.javalin.http.Handler;\n" +
        "import io.javalin.security.RouteRole;\n" +

        "public class " + SIMPLE_CLASS_NAME + " {\n" +
        "    public " + SIMPLE_CLASS_NAME + "(){}\n" +
        String.join("", controllersRound.handlersField) +
        "    public void " + METHOD_NAME
        + "(Javalin javalin, Consumer<JavalinFlyConfig> configFun) {\n" +
        "        JavalinFlyConfig config = new JavalinFlyConfig();\n" +
        "        configFun.accept(config);\n" +
        "        {\n" +
        "            OpenApiTranslator openApiTranslator = new OpenApiTranslator();\n" +
        String.join("", controllersRound.openApiStatements) +
        "            OpenApi openApi = openApiTranslator.build();\n" +
        "            config.openapi.edit(openApi);\n" +
        "            String openApiSpec = openApiTranslator.asString(openApi);\n" +
        "            Vars.openApiSpec(openApiSpec);\n"+
        "            Vars.swaggerUi(SwaggerUIHtmlGenerator.generateSwaggerUIHtml(Vars.openApiSpec()));\n" +
        "        }\n" +
        "        {\n" +
        "            javalin.addHandler(HandlerType.GET, \"/openapi\", ctx -> {\n" +
        "                 ctx.html(Vars.swaggerUi());\n" +
        "            });\n" +
        "        }\n" +
        String.join("", controllersRound.endpoints) +
        "    }\n" +
        "}\n";

    Javalin app ;

    try {
      JavaFileObject sourceFile = filer.createSourceFile(FULL_CLASS, element);
      try (Writer writer = sourceFile.openWriter()) {
        writer.write(source);
      }

    } catch (IOException e) {
      messager.error(element, "Error generating class %s: %s", FULL_CLASS, e.getMessage());
    }
  }


}
