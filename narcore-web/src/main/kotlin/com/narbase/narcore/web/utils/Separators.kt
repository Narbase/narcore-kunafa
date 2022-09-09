package com.narbase.narcore.web.utils

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.view
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


fun View?.horizontalSeparator(separatorWidth: Int = 1) = view {
    style {
        width = separatorWidth.px
        height = matchParent
        backgroundColor = Color(AppColors.borderColorHex)
    }
}

fun View?.verticalSeparator(separatorHeight: Int = 1, verticalMargins: Int = 0) = view {
    style {
        width = matchParent
        height = separatorHeight.px
        marginTop = verticalMargins.px
        marginBottom = verticalMargins.px
        backgroundColor = Color(AppColors.borderColorHex)
    }
}
