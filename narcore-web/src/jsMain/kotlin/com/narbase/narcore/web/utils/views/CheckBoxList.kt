package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.scrollable.ScrollableView
import com.narbase.narcore.web.utils.scrollable.scrollable

class CheckBoxList<T>(
    private var items: Array<SelectableItem<T>>,
    private var itemToString: (T) -> String,
    private val onSelection: (T) -> Unit,
    private val isEnabled: Boolean = true,
    private val style: Style,
) : Component() {

    constructor(
        itemsList: Array<T>,
        itemToString: (T) -> String,
        onSelection: (T) -> Unit,
        isEnabled: Boolean = true,
        style: Style,
    ) : this(
        itemsList.map { SelectableItem(it, false) }.toTypedArray(),
        itemToString,
        onSelection,
        isEnabled,
        style
    )

    private var checkBoxListLayout: ScrollableView? = null
    var checkboxes = mutableListOf<Checkbox>()

    override fun View?.getView() = verticalLayout {
        style {
            width = matchParent
            height = matchParent
        }

        checkBoxListLayout = scrollable {
            style {
                width = matchParent
            }
            if (items.isNullOrEmpty()) {

                textView {
                    style {
                        width = matchParent
                        height = wrapContent
                        padding = 8.px
                        fontSize = 14.px
                        color = AppColors.textInactive
                    }
                    text = "No Items found".localized()
                    onClick = { }
                }

            } else {
                items.forEach { selectableItem ->

                    verticalLayout {
                        val checkMark by lazy {
                            verticalLayout {
                                style {
                                    width = matchParent
                                    height = matchParent
                                    border = "1px solid ${Color.white}"
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
                                }
                                mount(checkMark)
                                if (selectableItem.isSelected) {
                                    checkMark.addRuleSet(checkedBackground)
                                } else {
                                    checkMark.addRuleSet(unCheckedBackground)
                                }
                            }
                        }
                        style {
                            width = matchParent
                            height = wrapContent
                        }
                        val checkbox by lazy {
                            detached.checkbox {
                                element.disabled = isEnabled.not()
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
                                alignItems = Alignment.Baseline
                                padding = "4px 0px".dimen()
                            }
                            mount(checkbox)
                            checkboxes.add(checkbox)

                            textView {
                                style {
                                    width = weightOf(1)
                                    height = wrapContent
                                    textAlign = style.textAlign
                                }
                                addRuleSet(checkBoxTextStyle)
                                text = itemToString(selectableItem.item)
                            }
                            onClick = { checkbox.element.click() }
                        }
                    }

                }
            }

        }
    }

    private val checkBoxTextStyle = classRuleSet {
        padding = 8.px
        fontSize = 14.px
        cursor = "pointer"
    }


    val checkedBackground = classRuleSet {
        backgroundColor = AppColors.narcoreColor
    }
    val unCheckedBackground = classRuleSet {
        backgroundColor = Color.white
    }

    class Style {
        var textAlign = TextAlign.Center
    }
}

class SelectableItem<T>(val item: T, var isSelected: Boolean = false)

@Suppress("unused")
fun <T> LinearLayout.setupCheckBoxList(
    list: Array<T>,
    itemToString: (T) -> String,
    style: CheckBoxList.Style.() -> Unit = {},
    isEnabled: Boolean = true,
    onSelection: (T) -> Unit,
): CheckBoxList<T> {
    return CheckBoxList(list, itemToString, onSelection, isEnabled, CheckBoxList.Style().apply(style)).apply {
        this@setupCheckBoxList.mount(this)
    }
}

fun <T> LinearLayout.setupCheckBoxList(
    list: Array<SelectableItem<T>>,
    itemToString: (T) -> String,
    style: CheckBoxList.Style.() -> Unit = {},
    isEnabled: Boolean = true,
    onSelection: (T) -> Unit,
): CheckBoxList<T> {
    return CheckBoxList(list, itemToString, onSelection, isEnabled, CheckBoxList.Style().apply(style)).apply {
        this@setupCheckBoxList.mount(this)
    }
}
