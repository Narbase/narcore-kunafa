package com.narbase.narcore.web.utils.scrollable

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.verticalLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.percent

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/09/25.
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