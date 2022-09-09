package com.narbase.narcore.web.views.user.profile

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.common.AppFontSizes

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

class EditablePhoneNumberView : Component() {
    private var callingCode: TextInput? = null
    private var phoneNumberInput: TextInput? = null
    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        disableEdit()
    }

    override fun View?.getView() = horizontalLayout {
        style {
            alignItems = Alignment.Center
        }
        textView {
            text = "+"
            addRuleSet(readOnlyRuleSet)
        }
        callingCode = textInput {
            text = code
            element.disabled = true
            element.size = 3
            style {
                borderRadius = 4.px
                width = wrapContent
            }
        }
        phoneNumberInput = textInput {
            text = code
            element.disabled = true
            style {
                width = wrapContent
                borderRadius = 4.px
            }
        }
    }

    var code: String
        get() = callingCode?.text ?: ""
        set(value) {
            callingCode?.text = value.trim().trim('+')
        }

    var phoneNumber: String
        get() = phoneNumberInput?.text ?: ""
        set(value) {
            phoneNumberInput?.text = value
        }

    fun enableEdit() {
        callingCode?.removeRuleSet(readOnlyRuleSet)
        callingCode?.addRuleSet(editableRueSet)
        callingCode?.element?.disabled = false

        phoneNumberInput?.removeRuleSet(readOnlyRuleSet)
        phoneNumberInput?.addRuleSet(editableRueSet)
        phoneNumberInput?.element?.disabled = false
    }

    fun disableEdit() {
        callingCode?.addRuleSet(readOnlyRuleSet)
        callingCode?.removeRuleSet(editableRueSet)
        callingCode?.element?.disabled = true

        phoneNumberInput?.addRuleSet(readOnlyRuleSet)
        phoneNumberInput?.removeRuleSet(editableRueSet)
        phoneNumberInput?.element?.disabled = true
    }

    companion object {
        private val readOnlyRuleSet by lazy {
            classRuleSet {
                cursor = "default"
                border = "none"
                padding = "0px 0px".dimen()
//                padding = "4px 0px".asDimension()
                backgroundColor = Color.transparent
                color = Color.black
                fontSize = AppFontSizes.normalText
            }
        }

        private val editableRueSet by lazy {
            classRuleSet {
                cursor = "text"
                padding = 6.px
                marginStart = 4.px
                border = "1px solid ${AppColors.textInputBorderColor}"
                backgroundColor = Color.transparent
                color = Color.black
                fontSize = AppFontSizes.normalText
            }
        }
    }
}

