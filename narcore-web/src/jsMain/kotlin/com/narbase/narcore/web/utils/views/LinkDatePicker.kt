@file:Suppress("JoinDeclarationAndAssignment", "MemberVisibilityCanBePrivate")

package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.common.AppColors

import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import kotlin.js.Date

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class LinkDatePicker(
    private val name: String,
    private val placeholder: String,
    private val controller: LinkDatePickerListViewController,
    private val onDateSelected: (Date?) -> Unit
) : Component() {

    var datePicker: DatePicker? = null
    private var dropDownTextView: TextView? = null
    private var boundTextInput: TextInput? = null
    private var listIcon: MaterialIcon? = null

    init {
        controller.isClear.observe { isClear ->
            isClear ?: return@observe
            if (isClear) {
                dropDownTextView?.text = placeholder
                listIcon?.apply {
                    removeRuleSet(cancelIconClass)
                    iconName = "arrow_drop_down"
                    onClick = { }
                }
            } else {
                listIcon?.apply {
                    iconName = "cancel"
                    addRuleSet(cancelIconClass)
                    onClick = { e ->
                        onDateSelected(null)
                        controller.setClear()
                        e.stopPropagation()
                    }
                }
            }
        }
    }

    private var cancelIconClass = classRuleSet {
        hover {
            color = AppColors.redLight
        }
    }

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        datePicker = DatePicker(field = boundTextInput?.element as HTMLInputElement, onSelect = { selectedDate ->
            dropDownTextView?.text = selectedDate.asDynamic().toDateString().toString()
            selectedDate.getTime()
            controller.setSelected()
            onDateSelected(selectedDate)
        })
        controller.onViewCreated()
    }

    override fun View?.getView() = verticalLayout {
        style {
            width = 200.px
            marginEnd = 16.px
        }
        textView {
            text = name
            style {
                width = wrapContent
                height = wrapContent
                fontSize = 12.px
                color = AppColors.textVeryLightGrey
            }
        }

        horizontalLayout {

            style {
                width = matchParent
                backgroundColor = Color.white
                marginTop = 4.px
                fontSize = 14.px
                padding = 4.px
                borderRadius = 3.px
                border = "0.5px solid #959595"
                cursor = "pointer"
                onClick = {
                    boundTextInput?.element?.let { inputElement ->
                        fireClick(inputElement)
                    }
                }
                color = AppColors.text
                alignItems = Alignment.Center
            }
            dropDownTextView = textView {
                style {
                    text = placeholder
                    width = weightOf(1)
                }
            }
            listIcon = materialIcon("arrow_drop_down") {
                size = MaterialIcon.md18
            }


        }
        boundTextInput = textInput {
            style {
                width = 0.px
                maxWidth = 0.px
                height = 0.px
                maxHeight = 0.px
                border = "none"
                overflow = "visible"
            }
        }
    }

    fun fireClick(element: HTMLInputElement) {
        if (element.asDynamic().fireEvent != null) {
            element.asDynamic().fireEvent("onclick")
        } else {
            val event = document.createEvent("Events")
            event.initEvent("click", true, false)
            element.dispatchEvent(event)
        }
    }
}

class LinkDatePickerListViewController {
    val isClear: Observable<Boolean> = Observable()

    fun onViewCreated() {
        setClear()
    }


    fun setSelected() {
        isClear.value = false
    }

    fun setClear() {
        isClear.value = true
    }
}

fun LinearLayout.linkDatePicker(
    name: String,
    placeholder: String,
    onDateSelected: (Date?) -> Unit
): LinkDatePicker {

    return LinkDatePicker(
        name, placeholder,
        LinkDatePickerListViewController(), onDateSelected
    ).apply { this@linkDatePicker.mount(this) }
}
