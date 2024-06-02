package com.narbase.narcore.web.utils.views.customViews

import com.narbase.kunafa.core.components.Component
import com.narbase.kunafa.core.components.TextInput
import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.textInput
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.narcore.web.common.AppColors

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

class EditableTextView(val block: TextInput.() -> Unit) : Component() {
    private var input: TextInput? = null
    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        disableEdit()
    }

    override fun View?.getView(): View {
        val view = textInput {
            block()
        }
        input = view
        input?.element?.disabled = true
        return view
    }

    var text: String
        get() = input?.text ?: ""
        set(value) {
            input?.text = value
        }


    fun enableEdit() {
        input?.removeRuleSet(readOnlyRuleSet)
        input?.addRuleSet(editableRueSet)
        input?.element?.disabled = false
    }

    fun disableEdit() {
        input?.addRuleSet(readOnlyRuleSet)
        input?.removeRuleSet(editableRueSet)
        input?.element?.disabled = true
    }

    companion object {
        val readOnlyRuleSet by lazy {
            classRuleSet {
                cursor = "default"
                border = "1px solid ${Color.transparent}"
                backgroundColor = Color.transparent
                color = Color.black
                borderRadius = 4.px
            }
        }

        val editableRueSet by lazy {
            classRuleSet {
                cursor = "text"
                border = "1px solid ${AppColors.textInputBorderColor}"
                backgroundColor = Color.transparent
                color = Color.black
                borderRadius = 4.px
            }
        }
    }
}

fun View.editableTextView(block: TextInput.() -> Unit): EditableTextView {
    return EditableTextView(block).apply { this@editableTextView.mount(this) }
}
