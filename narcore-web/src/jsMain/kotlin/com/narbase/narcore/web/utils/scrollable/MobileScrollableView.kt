package com.narbase.narcore.web.utils.scrollable

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.verticalLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.percent

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class MobileScrollableView(val parentBlock: (View.() -> Unit)?, val block: View.() -> Unit) : ScrollableView() {
    override var childView: View? = null

    override fun refreshScrollHandler() {
    }

    override fun View?.getView() = verticalLayout {
        style {
            width = matchParent
            height = matchParent
            overflow = "hidden"
            position = "relative"
        }

        parentBlock?.invoke(this)
        childView = verticalLayout {
            style {
                width = 100.percent
                height = 100.percent
                overflowY = "auto"
                boxSizing = "content-box"
            }

            block()
        }
    }
}