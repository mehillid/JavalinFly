package com.github.unldenis.javalinfly.kotlin

import com.github.unldenis.javalinfly.Response
import com.github.unldenis.javalinfly.openapi.model.OpenApi
import com.github.unldenis.javalinfly.processor.JavalinFlyConfig

fun <T, E> ok(value : T) : Response<T, E> = Response.ok(value)

fun <T, E> err(error : E) : Response<T, E> = Response.err(error)

fun JavalinFlyConfig.openapi(builder : OpenApi.() -> Unit) {
    this.openapi = JavalinFlyConfig.OpenApiFun {
        builder.invoke(it)
    }
}