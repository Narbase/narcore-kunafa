package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.visit
import com.narbase.kunafa.core.lifecycle.LifecycleObserver
import kotlinx.browser.document
import org.w3c.dom.HTMLTextAreaElement

class TextArea(parent: View? = null) : View(parent) {

    override val element: HTMLTextAreaElement = document.createElement("textarea") as HTMLTextAreaElement

    var text
        get() = element.value
        set(value) {
            element.value = value
        }

    var placeholder
        get() = element.placeholder
        set(value) {
            element.placeholder = value
        }
}

fun View?.textArea(lifecycleObserver: LifecycleObserver? = null, block: TextArea.() -> Unit): TextArea =
    TextArea(this).visit(lifecycleObserver, block)
