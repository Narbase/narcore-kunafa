package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.horizontalLayout
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.JustifyContent
import com.narbase.kunafa.core.css.classRuleSet
import com.narbase.kunafa.core.css.justifyContent
import com.narbase.kunafa.core.css.width
import com.narbase.kunafa.core.dimensions.dependent.matchParent

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun View?.horizontalWrapper(block: LinearLayout.() -> Unit) =
    horizontalLayout {
        addRuleSet(horizontalWrapperStyle)
        block()
    }


private val horizontalWrapperStyle by lazy {
    classRuleSet {
        width = matchParent
        justifyContent = JustifyContent.Center
    }
}
