package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.TextView
import com.narbase.kunafa.core.components.horizontalLayout
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.components.textView
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.storage.bidirectionalView
import com.narbase.narcore.web.translations.localized


import kotlin.math.min

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

class PaginationControls(
    private val onNextPage: () -> Unit,
    private val onPreviousPage: () -> Unit,
    val darkTheme: Boolean = false
) {
    private var paginationTextView: TextView? = null
    private var beforeIcon: MaterialIcon? = null
    private var afterIcon: MaterialIcon? = null

    private val activeIconStyle = if (darkTheme)
        classRuleSet {
            color = AppColors.white
            hover {
                color = Color.white
                backgroundColor = AppColors.separatorDark
                cursor = "pointer"
            }
        } else
        classRuleSet {
            color = AppColors.text
            hover {
                color = Color.black
                backgroundColor = AppColors.separatorLight
                cursor = "pointer"
            }
        }

    private val inactiveRuleSet = if (darkTheme) classRuleSet {
        color = AppColors.white
        opacity = 0.3
    }
    else classRuleSet {
        color = AppColors.separatorLight
    }

    fun LinearLayout.setup() {
        horizontalLayout {
            style {
                height = wrapContent
                alignSelf = Alignment.End
                padding = 8.px
                color = if (darkTheme)
                    AppColors.white
                else AppColors.text
                alignItems = Alignment.Center
            }
            paginationTextView = textView {
                text = "- to - out of -"
                style {
                    marginEnd = 16.px
                    marginStart = 16.px
                }
            }

            beforeIcon = materialIcon("keyboard_arrow_left") {
                bidirectionalView()
                tooltip("Previous")
            }

            afterIcon = materialIcon("keyboard_arrow_right") {
                bidirectionalView()
                tooltip("Next")
            }
        }
    }

    fun update(pageNo: Int, pageSize: Int, count: Int?) {
        count ?: return
        val paginationText = StringBuilder().apply {
            val startingIndex = pageSize * pageNo
            append("${startingIndex + if (startingIndex < count) 1 else 0} ")
            append("to".localized())
            append(" ${min(pageSize * (pageNo + 1), count)} ")
            append("out of".localized())
            append(" $count")
        }.toString()
        paginationTextView?.text = paginationText

        val isLastPage = pageSize * (pageNo + 1) >= count

        if (pageNo == 0) {
            beforeIcon?.setInactive()
        } else {
            beforeIcon?.setActive(onPreviousPage)
        }

        if (isLastPage) {
            afterIcon?.setInactive()
        } else {
            afterIcon?.setActive(onNextPage)
        }
    }

    private fun MaterialIcon.setInactive() {
        removeRuleSet(activeIconStyle)
        addRuleSet(inactiveRuleSet)
        onClick = {}
    }

    private fun MaterialIcon.setActive(onClickFunction: () -> Unit) {
        removeRuleSet(inactiveRuleSet)
        addRuleSet(activeIconStyle)
        onClick = { onClickFunction() }
    }
}

fun LinearLayout.setupPaginationControls(
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    darkTheme: Boolean = false
): PaginationControls {
    return PaginationControls(onNextPage, onPreviousPage, darkTheme).apply {
        setup()
    }
}
