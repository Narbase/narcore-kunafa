package com.narbase.narcore.web.utils.views.customViews

import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.components.verticalLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.utils.views.materialIcon
import com.narbase.narcore.web.utils.views.pointerCursor

fun LinearLayout.deleteButton(onDeleteClick: () -> Unit) = verticalLayout {
    val radius = 20
    style {
        width = radius.px
        height = radius.px
        backgroundColor = AppColors.redLight
        position = "absolute"
        borderRadius = (radius / 2).px
        top = (-radius / 2).px
        right = (-radius / 2).px
        alignItems = Alignment.Center
        justifyContent = JustifyContent.Center
        pointerCursor()
        hover {
            backgroundColor = AppColors.redDark
        }
    }
    onClick = { onDeleteClick.invoke() }
    materialIcon("clear") {
        style {
            color = Color.white
            fontSize = 14.px
        }
    }
}
