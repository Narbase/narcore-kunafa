@file:Suppress("JoinDeclarationAndAssignment", "MemberVisibilityCanBePrivate")

package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.percent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.common.AppFontSizes
import com.narbase.narcore.web.storage.StorageManager
import com.narbase.narcore.web.utils.PopupZIndex
import com.narbase.narcore.web.utils.colors.gray
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import kotlin.js.Date

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class DatePicker(
    field: HTMLInputElement? = null,
    trigger: HTMLElement? = null,
    container: HTMLElement? = null,
    onSelect: ((Date) -> Unit)? = null,
    bound: Boolean = false,
    theme: String? = null,
    defaultDate: Date = Date(),
    i18n: I18n? = null

) {
    val params: dynamic
    val picker: Pikaday

    init {
        refreshTheme(theme)
        params = object {}
        field?.let { params["field"] = it }
        trigger?.let { params["trigger"] = it }
        onSelect?.let { params["onSelect"] = it }
        container?.let { params["container"] = it }
        bound.let { params["bound"] = it }
        theme.let { params["theme"] = it }
        defaultDate.let { params["defaultDate"] = it }
        i18n.let { params["i18n"] = it }
        params["yearRange"] = arrayOf(1900, 2099)
        params["keyboardInput"] = false
        picker = Pikaday(params)
    }

    fun refreshTheme(theme: String?) {
        when (theme) {
            "dark-theme" -> createDarkThemeStyles()
            "light-theme" -> createLightThemeStyles()
        }
    }

    fun createDarkThemeStyles() {
        try {
            stringRuleSet(".dark-theme.pika-single") {
                borderRadius = 4.px
                border = "1px solid ${AppColors.lightDarkBackground}"
                backgroundColor = AppColors.lightDarkBackground
                fontFamily = StorageManager.language.appFontFamily
                width = matchParent
                zIndex = PopupZIndex.getTopIndex()
                color = AppColors.white
            }
            stringRuleSet(".dark-theme .pika-lendar") {
                width = wrapContent
                float = "none"
                color = AppColors.white
                backgroundColor = AppColors.lightDarkBackground
            }
            stringRuleSet(".dark-theme .pika-next") {
                backgroundImage = "url(/public/img/next.png)"
            }

            stringRuleSet(".dark-theme .pika-prev") {
                backgroundImage = "url(/public/img/next.png)"
                transform = "rotate(180deg)"
            }
            stringRuleSet(".dark-theme.pika-title") {
                borderBottom = "thin solid ${AppColors.lightDarkSeparator}"
                backgroundColor = AppColors.lightDarkBackground
            }
            stringRuleSet(".dark-theme .pika-label") {
                fontWeight = "normal"
                color = AppColors.white
                backgroundColor = AppColors.lightDarkBackground
            }
            stringRuleSet(".dark-theme .pika-table th") {
                fontWeight = "medium"
                color = AppColors.white
                backgroundColor = AppColors.lightDarkBackground
            }
            stringRuleSet(".dark-theme .pika-table abbr") {
                textDecoration = "none"
                color = AppColors.white
                backgroundColor = AppColors.lightDarkBackground
            }
            stringRuleSet(".dark-theme .pika-button") {
                backgroundColor = Color.white
                textAlign = TextAlign.Center
                borderColor = Color("31c1c3")
                width = 20.px
                height = 20.px
                fontSize = AppFontSizes.extraSmallText
                padding = "unset".dimen()
                color = AppColors.white
                backgroundColor = AppColors.lightDarkBackground
            }
            stringRuleSet(".dark-theme .pika-button:hover") {
                backgroundColor = gray(0.97)
                borderRadius = 50.percent
                boxShadow = "0px 0px #31c1c3"
                color = AppColors.black
            }
            stringRuleSet(".dark-theme .is-selected .pika-button") {
                backgroundColor = Color("31c1c3")
                boxShadow = "0px 0px #31c1c3"
                borderRadius = 50.percent
                borderColor = Color.white
            }
            stringRuleSet(".dark-theme .is-selected .pika-button:hover") {
                color = Color.white
            }
            stringRuleSet(".dark-theme .is-today .pika-button") {
                border = "1px solid ${AppColors.white}"
                borderRadius = 50.percent
                color = AppColors.white
            }
        } catch (e: dynamic) {
            console.log(e)
        }
    }

    fun createLightThemeStyles() {
        try {
            stringRuleSet(".pika-single.light-theme") {
                borderRadius = 4.px
                border = "1px solid ${AppColors.borderColor}"
                backgroundColor = AppColors.white
                fontFamily = StorageManager.language.appFontFamily
                zIndex = PopupZIndex.getTopIndex()
                color = AppColors.black
            }
            stringRuleSet(".light-theme .pika-lendar") {
                color = AppColors.white
                backgroundColor = AppColors.white
            }
            stringRuleSet(".light-theme .pika-next") {
                backgroundImage = "url(/public/img/bread.png)"
            }

            stringRuleSet(".light-theme .pika-prev") {
                backgroundImage = "url(/public/img/bread.png)"
                transform = "rotate(180deg)"
            }
            stringRuleSet(".light-theme .pika-title") {
                borderBottom = "thin solid ${AppColors.borderColor}"
                backgroundColor = AppColors.white
            }
            stringRuleSet(".light-theme .pika-label") {
                fontWeight = "normal"
                color = AppColors.black
                backgroundColor = AppColors.white
            }
            stringRuleSet(".light-theme .pika-table th") {
                fontWeight = "medium"
                color = AppColors.black
                backgroundColor = AppColors.white
            }
            stringRuleSet(".light-theme .pika-table abbr") {
                textDecoration = "none"
                color = AppColors.black
                backgroundColor = AppColors.white
            }
            stringRuleSet(".light-theme .pika-button") {
                backgroundColor = Color.white
                textAlign = TextAlign.Center
                borderColor = Color("31c1c3")
                width = 20.px
                height = 20.px
                fontSize = AppFontSizes.extraSmallText
                padding = "unset".dimen()
                color = AppColors.black
            }
            stringRuleSet(".light-theme .pika-button:hover") {
                backgroundColor = gray(0.97)
                borderRadius = 50.percent
                boxShadow = "0px 0px #31c1c3"
                color = AppColors.black
            }
            stringRuleSet(".light-theme .is-selected .pika-button") {
                backgroundColor = Color("31c1c3")
                boxShadow = "0px 0px #31c1c3"
                borderRadius = 50.percent
                borderColor = Color.white
                color = Color.white
            }
        } catch (e: dynamic) {
            console.log(e)
        }
    }
}

class I18n(
    val previousMonth: String = "Previous Month",
    val nextMonth: String = "Next Month",
    val months: Array<String>,
    val weekdays: Array<String>,
    val weekdaysShort: Array<String>
)

@JsModule("pikaday")
@JsNonModule
external class Pikaday(params: dynamic) {
    fun setDate(date: Date, preventOnSelect: Boolean): Unit
}

fun View?.datePicker(
    fieldElement: HTMLInputElement? = null,
    containerElement: HTMLElement? = null,
    theme: String,
    bound: Boolean = false,
    onDateSelected: (date: Date) -> Unit
): DatePicker {
    return DatePicker(
        fieldElement,
        onSelect = {
            onDateSelected(it)
        },
        container = containerElement,
        bound = bound,
        theme = theme,
        i18n = I18n(
            months = arrayOf(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
            ),
            weekdays = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
            weekdaysShort = arrayOf("S", "M", "T", "W", "T", "F", "S")
        )
    )
}


