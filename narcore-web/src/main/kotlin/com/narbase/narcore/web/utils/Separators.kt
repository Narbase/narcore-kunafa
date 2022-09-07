package com.narbase.narcore.web.utils

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.view
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2019] Narbase Technologies
 * All Rights Reserved.
 * Created by Mohammad Abbas
 * On: 12/8/19.
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
