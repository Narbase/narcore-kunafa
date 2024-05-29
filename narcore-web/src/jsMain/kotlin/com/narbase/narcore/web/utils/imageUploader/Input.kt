package com.narbase.narcore.web.utils.imageUploader

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.visit
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement

class Input(parent: View? = null) : View(parent) {
    override val element: HTMLInputElement = document.createElement("input") as HTMLInputElement
    var type
        get() = element.type
        set(value) {
            element.type = value
        }
}

fun View.input(block: Input.() -> Unit): Input = Input(this).visit(null, block)
