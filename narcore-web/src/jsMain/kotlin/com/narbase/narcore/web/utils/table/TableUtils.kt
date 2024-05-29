package com.narbase.narcore.web.utils.table

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.Dimension
import com.narbase.kunafa.core.dimensions.LinearDimension
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors


import com.narbase.narcore.web.utils.views.pointerCursor

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


fun LinearLayout.listTable(headerCells: LinearLayout.() -> Unit): LinearLayout {
    var tableLayout: LinearLayout? = null
    verticalLayout {
        style {
            width = matchParent
            height = wrapContent
        }
        verticalLayout {
            style {
                border = "1px solid #d4d4d4"
                borderRadius = 4.px
                marginTop = 16.px
                width = matchParent
                height = wrapContent
            }
            tableHeader(headerCells)
            tableLayout = verticalLayout {
                style {
                    width = matchParent
                    height = wrapContent
                }
            }
        }
    }
    return tableLayout!!
}

fun LinearLayout.tableHeader(cells: LinearLayout.() -> Unit) {
    verticalLayout {
        style {
            width = matchParent
        }
        verticalLayout {
            style {
                width = matchParent
                height = wrapContent
            }
            horizontalLayout {
                style {
                    width = matchParent
                }

                cells()
            }
            tableHeaderSeparator()
        }
    }
}

fun LinearLayout.headerCell(title: String, weight: Int, alignEnd: Boolean = false, isGrey: Boolean = false) =
    headerCell(title, { weightOf(weight) }, alignEnd, isGrey)

fun LinearLayout.headerCell(
    title: String,
    widthDimension: RuleSet.() -> Dimension,
    alignEnd: Boolean = false,
    isGrey: Boolean = false
) = horizontalLayout {
    id = title
    style {
        width = widthDimension()
        alignSelf = Alignment.Stretch
        paddingStart = 8.px
        paddingEnd = 8.px
        paddingTop = 8.px
        paddingBottom = 8.px
        justifyContent = if (alignEnd) JustifyContent.End else JustifyContent.Start
        if (isGrey) {
            backgroundColor = AppColors.extraLightBackground
        }
    }
    textView {
        text = title
        style {
            width = wrapContent
            color = AppColors.textDarkGrey
            fontSize = 14.px
        }
    }
}

fun LinearLayout.tableHeaderSeparator() {
    view {
        style {
            width = matchParent
            height = 1.px
            backgroundColor = AppColors.borderDarkColor
        }
    }
}


fun LinearLayout.tableCell(
    title: String,
    weight: Int,
    textFontSize: LinearDimension = 14.px,
    id: String = "",
    className: String = "",
    grey: Boolean = false
) {
    tableCell(weight, grey) {
        textView {
            this.id = id
            this.element.className = className
            text = title
            style {
                width = matchParent
                fontSize = textFontSize
                color = AppColors.black
            }
        }

    }
}


fun LinearLayout.tableCell(weight: Int, grey: Boolean = false, block: View.() -> Unit) =
    tableCell({ weightOf(weight) }, grey, block)

fun LinearLayout.tableCell(widthDimension: RuleSet.() -> Dimension, grey: Boolean = false, block: View.() -> Unit) =
    horizontalLayout {
        style {
            flexWrap = "wrap"
            width = widthDimension()
            alignSelf = Alignment.Stretch
            paddingStart = 8.px
            paddingEnd = 8.px
            paddingBottom = 8.px
            paddingTop = 8.px
            if (grey) {
                backgroundColor = AppColors.extraLightBackground
            }
        }
        block()
    }

fun View.tableRow(isClickable: Boolean = true, cells: LinearLayout.() -> Unit) = verticalLayout {
    element.className = "tableRow"
    style {
        width = matchParent
        height = wrapContent
        if (isClickable) {
            pointerCursor()
            hover {
                backgroundColor = Color(200, 200, 200, 0.1)
            }
        }
    }
    horizontalLayout {
        style {
            width = matchParent
        }
        cells()
    }
}
