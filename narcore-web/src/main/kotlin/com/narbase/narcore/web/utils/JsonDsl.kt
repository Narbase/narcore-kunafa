package com.narbase.narcore.web.utils

import kotlin.js.Json

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/03/19.
 */


fun json(block: JsonBuilder.() -> Unit): Json {
    val res: dynamic = js("({})")
    JsonBuilder(res).block()
    return res

}

class JsonBuilder(private val jsonObject: dynamic) {
    infix fun String.to(value: Any?) {
        jsonObject[this] = value
    }

    infix fun String.to(block: JsonBuilder.() -> Unit) {
        jsonObject[this] = json(block)
    }

    fun jsonArray(vararg items: JsonBuilder.() -> Unit): Array<Json> {
        return items.map { json(it) }.toTypedArray()
    }
}
