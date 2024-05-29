package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import kotlinx.dom.addClass

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun View.addClass(className: String) = element.addClass(className)