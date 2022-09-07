package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.visit
import com.narbase.kunafa.core.lifecycle.LifecycleObserver
import kotlinx.browser.document
import org.w3c.dom.HTMLIFrameElement

class IFrame(parent: View? = null) : View(parent) {
    override val element: HTMLIFrameElement = (document.createElement("iframe") as HTMLIFrameElement)
}

fun View?.iframe(lifecycleObserver: LifecycleObserver? = null, block: IFrame.() -> Unit): IFrame =
    IFrame(this).visit(lifecycleObserver, block)
