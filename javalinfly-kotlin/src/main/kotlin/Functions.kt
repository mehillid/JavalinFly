package com.github.unldenis.javalinfly.kotlin

import com.github.unldenis.javalinfly.Response

fun <T, E> ok(value : T) : Response<T, E> = Response.ok(value)

fun <T, E> err(error : E) : Response<T, E> = Response.err(error)