package com.narbase.narcore.web.network

import com.narbase.kunafa.core.components.View

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2018] Narbase Technologies
 * All Rights Reserved.
 * Created by ${user}
 * On: ${Date}.
 */

fun makeVisible(vararg views: View?) {
    views.forEach { it?.isVisible = true }
}

fun makeNotVisible(vararg views: View?) {
    views.forEach { it?.isVisible = false }
}
