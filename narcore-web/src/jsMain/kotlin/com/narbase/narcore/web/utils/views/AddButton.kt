package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.button
import com.narbase.kunafa.core.components.horizontalLayout
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors

fun LinearLayout.addButton(onButtonClicked: () -> Unit) {
    horizontalLayout {
        button {
            text = "+ ADD"
            style {
                backgroundColor = AppColors.main
                color = Color.white
                fontSize = 16.px
                padding = 8.px
                borderRadius = 4.px
                border = "none"

                hover {
                    backgroundColor = AppColors.mainDark
                    cursor = "pointer"

                }
            }
            onClick = { onButtonClicked() }
        }
    }
}
