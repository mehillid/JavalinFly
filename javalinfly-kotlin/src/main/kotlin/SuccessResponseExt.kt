package com.github.unldenis.javalinfly.kotlin

import com.github.unldenis.javalinfly.SuccessResponse

fun <E> successResponse(function: SuccessResponseDSL<E>.() -> Unit): SuccessResponse<E> {
    val dsl = SuccessResponseDSL<E>()

    try {
        function.invoke(dsl)
    } catch (e: LoopStopException) {
        //do nothing
    }

    return if (dsl.err == null) {
        SuccessResponse.ok()
    } else {
        SuccessResponse.err(dsl.err)
    }

}


class SuccessResponseDSL<E> {
    var err: E? by exitDelegate(null)
}
