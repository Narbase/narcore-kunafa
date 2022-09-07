package com.narbase.narcore.web.utils.views.customViews

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.percent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.translations.localized

import com.narbase.narcore.web.utils.views.pointerCursor
import com.narbase.narcore.web.utils.views.unSelectable

class CustomCheckBoxList<T>(
    private var itemsList: List<Items<T>>,
    private var itemToString: (T) -> String,
    private val onSelection: (T) -> Unit,
    private val itemTextStyle: RuleSet? = null,
    private val customItemStyle: RuleSet? = null,
    private val isEditable: Boolean = true,
    private val itemId: ((T) -> String)? = null
) : Component() {

    private var checkBoxListLayout: LinearLayout? = null

    override fun View?.getView() = verticalLayout {
        style {
            width = matchParent
            height = wrapContent
        }

        checkBoxListLayout = verticalLayout {
            style {
                width = matchParent
            }
            if (itemsList.isNullOrEmpty()) {

                textView {
                    style {
                        width = matchParent
                        height = wrapContent
                    }
                    addRuleSet(checkBoxTextStyle)
                    text = "No Items found".localized()
                    onClick = { }
                }

            } else {
                itemsList.forEach { selectableItem ->

                    verticalLayout {
                        val checkMark by lazy {
                            verticalLayout {
                                style {
                                    width = matchParent
                                    height = matchParent
                                }
                                if (selectableItem.isSelected) {
                                    addRuleSet(checkedBackground)
                                } else {
                                    addRuleSet(unCheckedBackground)
                                }
                            }
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
                                }
                                mount(checkMark)
                            }
                        }
                        style {
                            width = matchParent
                            height = wrapContent
                            pointerCursor()
                            unSelectable()
                        }
                        customItemStyle?.let { addRuleSet(it) }
                        val checkbox by lazy {
                            detached.checkbox {
                                id = itemId?.let { it(selectableItem.item) }
                                element.disabled = isEditable.not()
                                onChange = {
                                    selectableItem.isSelected = selectableItem.isSelected.not()
                                    onSelection(selectableItem.item)
                                    if (isChecked) {
                                        checkMark.removeRuleSet(unCheckedBackground)
                                        checkMark.addRuleSet(checkedBackground)
                                    } else {
                                        checkMark.removeRuleSet(checkedBackground)
                                        checkMark.addRuleSet(unCheckedBackground)
                                    }
                                }
                                isChecked = selectableItem.isSelected
                                style {
                                    height = 0.px
                                    width = 0.px
                                    display = "none"
                                }
                            }

                        }
                        horizontalLayout {
                            mount(customCheckbox)

                            style {
                                width = matchParent
                                height = wrapContent
                                alignItems = Alignment.Center
                            }
                            mount(checkbox)

                            textView {
                                style {
                                    width = weightOf(1)
                                    height = wrapContent
                                    marginStart = 8.px
                                }

                                if (itemTextStyle != null) addRuleSet(itemTextStyle) else addRuleSet(checkBoxTextStyle)

                                text = itemToString(selectableItem.item)
                            }
                            if (!selectableItem.isEnabled) {
                                offSwitch()
                                this.style { opacity = 0.6 }
                            }
                            onClick = { checkbox.element.click() }
                        }
                    }

                }
            }

        }
    }

    private fun View.offSwitch() {
        horizontalLayout {
            style {
                padding = "2px 4px".dimen()
                backgroundColor = AppColors.separatorLight
                borderRadius = 8.px
                alignItems = Alignment.Center
            }
            view {
                style {
                    width = 6.px
                    height = 6.px
                    backgroundColor = Color.red
                    marginEnd = 4.px
                    borderRadius = 50.percent
                }
            }
            textView {
                style {
                    fontSize = 8.px
                }
                text = "Off"
            }
        }

    }

    private val checkBoxTextStyle = classRuleSet {
        padding = 4.px
        fontSize = 14.px
        cursor = "pointer"
    }


    val checkedBackground = classRuleSet {
        backgroundColor = AppColors.narcoreColor
    }
    val unCheckedBackground = classRuleSet {
        backgroundColor = Color.transparent
    }
}

class Items<T>(val item: T, var isSelected: Boolean = false, val isEnabled: Boolean = true)

fun <T> View.setupCustomCheckBoxList(
    list: List<Items<T>>,
    itemToString: (T) -> String,
    onSelection: (T) -> Unit,
    itemTextStyle: RuleSet? = null,
    customItemStyle: RuleSet? = null,
    isEditable: Boolean = true,
    itemId: ((T) -> String)? = null
): CustomCheckBoxList<T> {
    return CustomCheckBoxList(
        list,
        itemToString,
        onSelection,
        itemTextStyle,
        customItemStyle,
        isEditable,
        itemId
    ).apply {
        this@setupCustomCheckBoxList.mount(this)
    }
}
