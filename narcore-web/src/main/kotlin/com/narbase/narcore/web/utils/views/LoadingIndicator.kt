package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.ImageView
import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.imageView
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.px

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun View?.loadingIndicator(): ImageView {
    return imageView {
        style {
            marginTop = 18.px
            width = 40.px
            height = 40.px
            alignSelf = Alignment.Center
        }
        element.src = "/public/img/loading.gif"
    }
}
