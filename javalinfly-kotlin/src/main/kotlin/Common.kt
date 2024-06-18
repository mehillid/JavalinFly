package com.github.unldenis.javalinfly.kotlin

import kotlin.properties.Delegates

object LoopStopException :
    Throwable(/* message = */ "Stop look", /* cause = */null, /* enableSuppression = */false, /* writableStackTrace = */false)

// lightweight throwable without the stack trace

fun <T> exitDelegate(init: T) = Delegates.observable(init) { _, oldValue, new ->
    if(oldValue == new) {
        return@observable
    }
    throw LoopStopException
}
