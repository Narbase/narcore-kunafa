package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.narcore.web.common.AppConfig
import com.narbase.narcore.web.utils.PopupZIndex
import disableBlurOptions
import tippy
import kotlin.js.json

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/03/06.
 */

@Suppress("ConstantConditionIf")
fun View.tooltip(message: String, delay: Int = 400) {
    if (AppConfig.isMobile.not()) {
        tippy(
            this.element, json(
                "content" to message,
                "delay" to arrayOf(delay, 0),
                "arrow" to "large",
                "zIndex" to PopupZIndex.getTopIndex(),
                disableBlurOptions
            )
        )
    }
}

