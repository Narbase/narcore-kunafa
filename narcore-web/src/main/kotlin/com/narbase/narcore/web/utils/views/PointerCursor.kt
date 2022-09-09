@file:Suppress("NOTHING_TO_INLINE")

package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.css.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

inline fun RuleSet.pointerCursor() {
    cursor = "pointer"
}


inline fun RuleSet.unSelectable() {
    /*
    -webkit-user-select: none; /* Safari */
    -moz-user-select: none; /* Firefox */
    -ms-user-select: none; /* IE10+/Edge */
    user-select: none;
     */

    try {
        this["-webkit-user-select"] = "none"
        this["-moz-user-select"] = "none"
        this["-ms-user-select"] = "none"
        this["user-select"] = "none"
    } catch (e: dynamic) {
        console.log(e)
    }
}

inline fun RuleSet.singleLine() {
    whiteSpace = "nowrap"
    textOverflow = "ellipsis"
    overflow = "hidden"
}
