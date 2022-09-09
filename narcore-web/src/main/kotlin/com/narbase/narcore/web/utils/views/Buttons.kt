package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.horizontalLayout
import com.narbase.kunafa.core.components.imageView
import com.narbase.kunafa.core.components.textView
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors


/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun View.deleteButton(
    customStyle: DeleteButtonStyle.() -> Unit = {},
    buttonText: String? = null,
    buttonTextCustomRuleSet: RuleSet? = null,
    buttonImageCustomRuleSet: RuleSet? = null,
    onButtonClicked: () -> Unit
) {
    val viewStyle = DeleteButtonStyle().apply { customStyle() }
    horizontalLayout {
        style {
            viewStyle.border?.let {
                border = "thin solid ${AppColors.borderColorHex}"
            }
            borderRadius = 18.px
            padding = "4px 16px".dimen()
            alignItems = Alignment.Center
            backgroundColor = Color.white
            pointerCursor()
        }

        onClick = { onButtonClicked() }

        imageView {
            style {
                width = 18.px
                height = 18.px
            }

            buttonImageCustomRuleSet?.let { addRuleSet(buttonImageCustomRuleSet) }

            element.src = "/public/img/delete-red.svg"
        }

        buttonText?.let {
            textView {
                style {
                    color = Color.red
                    fontSize = 12.px
                    marginStart = 6.px
                }

                buttonTextCustomRuleSet?.let { addRuleSet(it) }

                text = it
            }
        }
    }
}

class DeleteButtonStyle(
    var border: String? = "thin solid ${AppColors.borderColorHex}"
)
