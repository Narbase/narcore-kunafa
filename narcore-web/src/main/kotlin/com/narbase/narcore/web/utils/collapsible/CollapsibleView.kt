package com.narbase.narcore.web.utils.collapsible

import com.narbase.kunafa.core.components.Component
import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.verticalLayout
import com.narbase.kunafa.core.css.overflow
import com.narbase.kunafa.core.css.transition
import com.narbase.kunafa.core.css.width
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import kotlinx.browser.window

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class CollapsibleView(val expand: Boolean, val block: View?.() -> Unit) : Component() {
    var animationDuration = 0.2
    var collapsibleContentView: View? = null

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        super.onViewMounted(lifecycleOwner)
        if (expand) {
            val height = collapsibleContentView?.element?.scrollHeight
            if (height != 0) {
                collapsibleContentView?.element?.style?.maxHeight = "${height}px"
            }
        } else {
            collapsibleContentView?.element?.style?.maxHeight = "0px"
        }
    }

    override fun View?.getView(): View {
        val view = verticalLayout {
            style {
                width = matchParent
                transition = "max-height ${animationDuration}s ease-out"
                overflow = "hidden"
            }
            block()
        }
        collapsibleContentView = view
        return view
    }

    fun toggle() {
        if (collapsibleContentView?.element?.style?.maxHeight == "0px") {
            collapsibleContentView?.element?.style?.maxHeight = "${collapsibleContentView?.element?.scrollHeight}px"
            onExpand?.invoke()
            onExpandFinished?.let { onExpandFinished ->
                window.setTimeout({ onExpandFinished() }, (animationDuration * 1000).toInt())
            }

        } else {
            collapsibleContentView?.element?.style?.maxHeight = "0px"
            onCollapse?.invoke()
            onCollapseFinished?.let { onCollapseFinished ->
                window.setTimeout({ onCollapseFinished() }, (animationDuration * 1000).toInt())
            }

        }
        asDynamic()
    }

    var onCollapse: (() -> Unit)? = null
    var onExpand: (() -> Unit)? = null

    var onCollapseFinished: (() -> Unit)? = null
    var onExpandFinished: (() -> Unit)? = null
}


fun View?.collapsibleView(expand: Boolean = false, block: View?.() -> Unit): CollapsibleView {
    return CollapsibleView(expand, block).apply {
        this@collapsibleView?.mount(this)
    }
}
