package com.quicklink.javalinfly.kotlin

import com.quicklink.javalinfly.openapi.model.OpenApi
import com.quicklink.javalinfly.processor.JavalinFlyConfig


fun JavalinFlyConfig.openapi(builder: OpenApi.() -> Unit) {
    this.openapi = JavalinFlyConfig.OpenApiFun {
        builder.invoke(it)
    }
}