package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.horizontalLayout
import com.narbase.kunafa.core.components.textView
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors


/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

val baseStyle by lazy {

    classRuleSet {
        border = "1px solid rgba(0, 0, 0, 0.23)"
        borderRadius = 4.px
        padding = "8px 16px".dimen()
        width = wrapContent
        color = Color("1a729d")
        cursor = "pointer"
        alignItems = Alignment.Center
        hover {
            color = AppColors.mainDark
        }
    }
}

fun View.buttonWithImage(
    buttonText: String,
    materialIconName: String,
    backgroundStyle: (RuleSet.() -> Unit)? = null,
    onClickCallback: () -> Unit
) {
    horizontalLayout {
        addRuleSet(baseStyle)
        backgroundStyle?.let { style { it() } }
        onClick = { onClickCallback() }

        materialIcon(materialIconName) {
            style {
                fontSize = 16.px
            }

        }
        textView {
            text = buttonText
            style {
                fontSize = 16.px
                marginStart = 8.px
            }
        }
    }
}
