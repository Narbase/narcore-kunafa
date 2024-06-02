@file:Suppress("JoinDeclarationAndAssignment", "MemberVisibilityCanBePrivate")

import org.w3c.dom.HTMLElement
import kotlin.js.Json
import kotlin.js.json
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
@JsModule("tippy.js")
@JsNonModule
external object TippyModule {
    fun default(element: HTMLElement, option: dynamic): dynamic
}

fun tippy(element: HTMLElement, option: dynamic): dynamic = TippyModule.default(element, option)

val disableBlurOptions = "popperOptions" to json(
    "modifiers" to json(
        "computeStyle" to json(
            "gpuAcceleration" to false
        )
    )
)

class TippyOptions {
    val json = json()

    var interactive by JsonValue("interactive")
    var trigger by JsonValue("trigger")
    var theme by JsonValue("theme")
    var placement by JsonValue("placement")
    var arrow by JsonValue("arrow")
    var content by JsonValue("content")
    var zIndex by JsonValue("zIndex")
    var distance by JsonValue("distance")
    var maxWidth by JsonValue("maxWidth")
    var followCursor by JsonValue("followCursor")

    fun disableBlurOptions() {
        json["popperOptions"] = json(
            "modifiers" to json(
                "computeStyle" to json(
                    "gpuAcceleration" to false
                )
            )
        )

    }


    class JsonValue(val key: String) : ReadWriteProperty<TippyOptions, Any?> {
        override fun getValue(thisRef: TippyOptions, property: KProperty<*>): Any? = thisRef.json[key]

        override fun setValue(thisRef: TippyOptions, property: KProperty<*>, value: Any?) {
            thisRef.json[key] = value
        }

    }


}

fun tippyOptions(block: TippyOptions.() -> Unit): Json {
    return TippyOptions().apply(block).json
}

private fun getTippyOptions(content: HTMLElement) = json(
    "interactive" to true,
    "trigger" to "click",
    "theme" to "light-border",
    //  "placement" to bidirectional("right", "left"),
    "arrow" to "large",
    "content" to content
)