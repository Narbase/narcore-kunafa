package com.narbase.narcore.web.utils

import com.narbase.kunafa.core.components.TextInput
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.utils.Styles.disabledTextInput
import com.narbase.narcore.web.utils.Styles.enabledTextInput

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun TextInput.setEnabled(customRuleSet: RuleSet? = null) {
    element.disabled = false
    addRuleSet(customRuleSet ?: enabledTextInput)
}

fun TextInput.setDisabled(customRuleSet: RuleSet? = null) {
    element.disabled = true
    addRuleSet(customRuleSet ?: disabledTextInput)
}

object Styles {
    val disabledTextInput = classRuleSet {
        padding = "0px 3px".dimen()
        border = "1px solid ${Color.transparent}"
    }
    val enabledTextInput = classRuleSet {
        padding = "0px 3px".dimen()
        borderRadius = 2.px
        border = "1px solid ${AppColors.borderColor}"
        focus {
            border = "1px solid ${AppColors.focusInputBorderColor}"
        }
    }
}
