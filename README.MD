This repository includes an annotation processor designed to extend the functionality of the Javalin library by automatically generating OpenAPI documentation.
The goal is to simplify development while using zero reflection.

Features:
- Automatic OpenAPI Documentation: Automatically generate OpenAPI documentation for your Javalin routes.
- Zero Reflection: Improve performance and reduce complexity by avoiding reflection.
- Ease of Development: Simplify the development process with automatic documentation generation.

### Add dependency

Latest release

[![Version](https://img.shields.io/github/v/release/qlsolutions/JavalinFly?style=flat-square)](https://github.com/qlsolutions/JavalinFly/releases/latest)

#### Maven

```xml
<dependency>
    <groupId>com.github.qlsolutions.JavalinFly</groupId>
    <artifactId>javalinfly-core</artifactId>
    <version>${javalinfly-version}</version>
</dependency>
```

#### Gradle

```kotlin
implementation("com.github.qlsolutions.JavalinFly:javalinfly-core:$javalinflyVersion")
```

Make sure you include the <a href="https://jitpack.io/">repository</a> as well.