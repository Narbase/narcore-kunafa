package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.visit
import com.narbase.kunafa.core.lifecycle.LifecycleObserver
import kotlinx.browser.document
import org.w3c.dom.HTMLSourceElement
import org.w3c.dom.HTMLVideoElement

class Video(parent: View? = null) : View(parent) {
    override val element: HTMLVideoElement = (document.createElement("video") as HTMLVideoElement)
}

fun View?.video(lifecycleObserver: LifecycleObserver? = null, block: Video.() -> Unit): Video =
    Video(this).visit(lifecycleObserver, block)

class Source(parent: View? = null) : View(parent) {
    override val element: HTMLSourceElement = (document.createElement("source") as HTMLSourceElement)
}

fun View?.source(lifecycleObserver: LifecycleObserver? = null, block: Source.() -> Unit): Source =
    Source(this).visit(lifecycleObserver, block)
