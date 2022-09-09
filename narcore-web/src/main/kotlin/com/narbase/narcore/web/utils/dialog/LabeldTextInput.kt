package com.narbase.narcore.web.utils.dialog

import com.narbase.kunafa.core.components.TextInput
import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.textInput
import com.narbase.kunafa.core.components.textView
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.dimensions.st
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.common.AppFontSizes
import com.narbase.narcore.web.utils.views.TextArea
import com.narbase.narcore.web.utils.views.textArea

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


fun View.labeledTextInput(
    title: String,
    onChange: (() -> Unit)? = null,
    textInputOption: (TextInput.() -> Unit)? = null
): TextInput {
    titleField(title)
    val textInput = textInputField(title, textInputOption)
    onChange?.let { textInput.element.oninput = { onChange() } }
    return textInput
}

fun View.labeledTextArea(
    title: String,
    onChange: (() -> Unit)? = null,
    textAreaOption: (TextArea.() -> Unit)? = null
): TextArea {
    titleField(title)
    val textArea = textAreaField(title, textAreaOption)
    onChange?.let { textArea.element.onchange = { onChange() } }
    return textArea
}


fun View.titleField(title: String, textColor: Color = AppColors.black) {
    textView {
        style {
            color = textColor
            fontSize = AppFontSizes.smallText
            marginBottom = 8.px
            fontWeight = "bold"
        }
        text = title
    }
}

fun View.textInputField(title: String, textInputOption: (TextInput.() -> Unit)? = null) = textInput {
    placeholder = title
    style {
        width = matchParent
        marginBottom = 16.px
    }
    addRuleSet(textInputStyle)
    textInputOption?.invoke(this)
}

private fun View.textAreaField(title: String, textAreaOption: (TextArea.() -> Unit)? = null) = textArea {
    placeholder = title
    style {
        width = matchParent
        marginBottom = 16.px
    }
    addRuleSet(textAreaStyle)
    textAreaOption?.invoke(this)
}


val textInputStyle by lazy {
    classRuleSet {
        padding = 4.px
        fontSize = 14.px
//        padding = st("6px 12px")
        border = "1px solid ${AppColors.borderColor}"
        borderRadius = 4.px
        focus {
            border = "1px solid ${AppColors.focusInputBorderColor}"
        }
    }
}

val textAreaStyle by lazy {
    classRuleSet {
        padding = 4.px
        fontSize = 14.px
        padding = st("6px 12px")
        border = "1px solid ${AppColors.borderColor}"
        borderRadius = 4.px
        focus {
            border = "1px solid ${AppColors.focusInputBorderColor}"
        }
    }
}
val textInputErrorStyle by lazy {
    classRuleSet {
        padding = 4.px
        fontSize = 14.px
        padding = st("6px 12px")
        border = "1px solid ${AppColors.redLight}"
        borderRadius = 4.px
    }
}
