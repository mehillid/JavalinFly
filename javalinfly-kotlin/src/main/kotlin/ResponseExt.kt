package com.quicklink.javalinfly.kotlin

import com.quicklink.javalinfly.Response

fun <T, E> response(function: ResponseDSL<T, E>.() -> Unit): Response<T, E> {
    val dsl = ResponseDSL<T, E>()

    try {
        function.invoke(dsl)
    } catch (e: LoopStopException) {
        //do nothing
    }

    return if (dsl.ok != null) {
        Response.ok(dsl.ok)
    } else {
        Response.err(dsl.err)
    }
}



class ResponseDSL<T, E> {
    var ok: T? by exitDelegate(null)
    var err: E? by exitDelegate(null)
}
