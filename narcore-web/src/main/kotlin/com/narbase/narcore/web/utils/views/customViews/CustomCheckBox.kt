package com.narbase.narcore.web.utils.views.customViews

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.utils.views.pointerCursor
import com.narbase.narcore.web.utils.views.unSelectable

class CustomCheckBox(
    private val checkBoxText: String,
    var isSelected: Boolean,
    private val onSelection: ((Boolean) -> Unit)?,
    private val textStyle: RuleSet? = null,
    val id: String? = null
) : Component() {

    val checkMark by lazy {
        detached.verticalLayout {
            style {
                width = matchParent
                height = matchParent
            }
            if (isSelected) {
                addRuleSet(checkedBackground)
            } else {
                addRuleSet(unCheckedBackground)
            }
        }
    }

    override fun View?.getView() = verticalLayout {
        this@CustomCheckBox.id?.let { this@verticalLayout.id = it }
        style {
            width = wrapContent
            height = wrapContent
        }

        val customCheckbox by lazy {
            detached.verticalLayout {
                style {
                    height = 12.px
                    width = 12.px
                    alignItems = Alignment.Center
                    justifyContent = JustifyContent.Center
                    border = "thin solid ${AppColors.narcoreColor}"
                    padding = 2.px
                    margin = 4.px
                }
                mount(checkMark)
            }
        }

        horizontalLayout {
            mount(customCheckbox)

            style {
                width = matchParent
                height = wrapContent
                alignItems = Alignment.Center
                padding = "4px 0px".dimen()
                pointerCursor()
            }

            textView {
                text = checkBoxText
                style {
                    unSelectable()
                }
                if (textStyle != null) addRuleSet(textStyle)
            }

            onClick = { toggleCheckbox() }
        }
    }


    fun toggleCheckbox() {
        isSelected = isSelected.not()
        onSelection?.invoke(isSelected)
        if (isSelected) {
            checkMark.removeRuleSet(unCheckedBackground)
            checkMark.addRuleSet(checkedBackground)
        } else {
            checkMark.removeRuleSet(checkedBackground)
            checkMark.addRuleSet(unCheckedBackground)
        }
    }

    fun unCheck() {
        if (isSelected) toggleCheckbox()
    }

    fun check() {
        if (isSelected.not()) toggleCheckbox()
    }

    companion object {
        val checkedBackground by lazy {
            classRuleSet {
                backgroundColor = AppColors.narcoreColor
            }
        }
        val unCheckedBackground by lazy {
            classRuleSet {
                backgroundColor = Color.transparent
            }
        }
    }
}


fun View.customCheckBox(
    checkBoxText: String,
    isSelected: Boolean,
    textStyle: RuleSet? = null,
    id: String? = null,
    onSelection: ((Boolean) -> Unit)? = null
): CustomCheckBox {
    return CustomCheckBox(checkBoxText, isSelected, onSelection, textStyle, id = id).apply {
        this@customCheckBox.mount(this)
    }
}
