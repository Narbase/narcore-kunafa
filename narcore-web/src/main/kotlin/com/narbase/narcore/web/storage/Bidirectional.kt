package com.narbase.narcore.web.storage

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.css.transform
import com.narbase.narcore.web.common.models.Direction

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/05/10.
 */

fun <T> bidirectional(ltrValue: T, rtlValue: T): T {
    return if (StorageManager.language.direction == Direction.RTL) rtlValue else ltrValue
}

fun bidirectional(ltrValue: () -> Unit, rtlValue: () -> Unit) {
    if (StorageManager.language.direction == Direction.RTL) rtlValue() else ltrValue()
}

fun View.bidirectionalView() {
    style {
        transform = bidirectional("unset", "scaleX(-1)")
    }
}
