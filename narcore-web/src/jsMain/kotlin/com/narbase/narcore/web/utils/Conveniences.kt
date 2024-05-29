package com.narbase.narcore.web.utils

fun <T, R> Iterable<T>.mapToArray(transform: (T) -> R): Array<R> {
    return map { transform(it) }.toTypedArray()
}

fun Double.format(digits: Int): String = this.asDynamic().toFixed(digits)
fun Float.format(digits: Int): String = this.asDynamic().toFixed(digits)
