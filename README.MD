This repository includes an annotation processor designed to extend the functionality of the Javalin library by automatically generating OpenAPI documentation.
The goal is to simplify development while using zero reflection.

Features:
- Automatic OpenAPI Documentation: Automatically generate OpenAPI documentation for your Javalin routes.
- Zero Reflection: Improve performance and reduce complexity by avoiding reflection.
- Ease of Development: Simplify the development process with automatic javalin handlers injection.

### Add dependency

Latest release

[![Version](https://img.shields.io/github/v/release/qlsolutions/JavalinFly?style=flat-square)](https://github.com/qlsolutions/JavalinFly/releases/latest)

<details>
    <summary>Maven</summary>

Make sure you include the <a href="https://jitpack.io/">repository</a> as well.

- Dependency
   ```xml
    <dependency>
        <groupId>com.github.qlsolutions.JavalinFly</groupId>
        <artifactId>javalinfly-core</artifactId>
        <version>0.1.5</version>
    </dependency>       
   ```

- Annotation processor
   ```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
            <annotationProcessorPaths>
                <annotationProcessorPath>
                    <groupId>com.github.qlsolutions.JavalinFly</groupId>
                    <artifactId>javalinfly-core</artifactId>
                    <version>0.1.5</version>
                </annotationProcessorPath>
            </annotationProcessorPaths>
        </configuration>
    </plugin>
   ```

- <details>
  <summary>Optionally also the kotlin module</summary>

    ```xml
    <dependency>
        <groupId>com.github.qlsolutions.JavalinFly</groupId>
        <artifactId>javalinfly-kotlin</artifactId>
        <version>0.1.5</version>
    </dependency>
    ```

</details>


<details>
    <summary>Gradle</summary>

Make sure you include the <a href="https://jitpack.io/">repository</a> as well.


- Groovy DSL

    - Dependency

    ```groovy
    implementation 'com.github.qlsolutions.JavalinFly:javalinfly-core:0.1.5'
    ```

    - Annotation processor

    ```groovy
    annotationProcessor 'com.github.qlsolutions.JavalinFly:javalinfly-core:0.1.5'
    ```

    - <details>
        <summary>Optionally also the kotlin module</summary>
      
        ```groovy
        implementation 'com.github.qlsolutions.JavalinFly:javalinfly-kotlin:0.1.5'
        ```
      
        </details>



- Kotlin DSL

    - Dependency
    ```groovy
    implementation("com.github.qlsolutions.JavalinFly:javalinfly-core:0.1.5")
    ```

    - Annotation processor
    ```groovy
    annotationProcessor("com.github.qlsolutions.JavalinFly:javalinfly-core:0.1.5")
    ```

    - <details>
        <summary>Optionally also the kotlin module</summary>

        ```groovy
        implementation("com.github.qlsolutions.JavalinFly:javalinfly-kotlin:0.1.5")
        ```

        </details>

    
</details>
