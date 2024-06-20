This repository includes an annotation processor designed to extend the functionality of the Javalin library by automatically generating OpenAPI documentation.
The goal is to simplify development while using zero reflection.

Features:
- Automatic OpenAPI Documentation: Automatically generate OpenAPI documentation for your Javalin routes.
- Zero Reflection: Improve performance and reduce complexity by avoiding reflection.
- Ease of Development: Simplify the development process with automatic documentation generation.

### Add dependency
#### Maven

```xml
<dependency>
    <groupId>com.github.qlsolutions.JavalinFly</groupId>
    <artifactId>javalinfly-core</artifactId>
    <version>${VERSION}</version>
</dependency>
```

#### Gradle

```kotlin
implementation("com.github.qlsolutions.JavalinFly:javalinfly-core:$VERSION")
```

Make sure you include the <a href="https://jitpack.io/">repository</a> as well.