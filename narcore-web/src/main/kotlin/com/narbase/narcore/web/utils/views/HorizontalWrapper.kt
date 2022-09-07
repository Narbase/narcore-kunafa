package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.horizontalLayout
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.JustifyContent
import com.narbase.kunafa.core.css.classRuleSet
import com.narbase.kunafa.core.css.justifyContent
import com.narbase.kunafa.core.css.width
import com.narbase.kunafa.core.dimensions.dependent.matchParent

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2019/06/01.
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
