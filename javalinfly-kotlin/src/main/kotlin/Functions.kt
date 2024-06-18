package com.github.unldenis.javalinfly.kotlin

import com.github.unldenis.javalinfly.Response
import com.github.unldenis.javalinfly.SuccessResponse
import com.github.unldenis.javalinfly.openapi.model.OpenApi
import com.github.unldenis.javalinfly.processor.JavalinFlyConfig


fun JavalinFlyConfig.openapi(builder: OpenApi.() -> Unit) {
    this.openapi = JavalinFlyConfig.OpenApiFun {
        builder.invoke(it)
    }
}