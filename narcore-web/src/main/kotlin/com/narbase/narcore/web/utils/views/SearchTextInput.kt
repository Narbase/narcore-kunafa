package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.horizontalLayout
import com.narbase.kunafa.core.components.imageView
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.components.textInput
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.px


/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun LinearLayout.searchTextInput(placeholderText: String, onKeyPress: (searchTerm: String) -> Unit) {
    val bouncer = DelayBouncer<String>(300)
    horizontalLayout {
        style {
            alignItems = Alignment.Center
            border = "1px solid #d4d4d4"
            borderRadius = 50.px
            padding = 6.px
        }

        imageView {
            style {
                alignSelf = Alignment.Center
                width = 16.px
                height = 16.px
            }

            element.src = "/public/img/search.png"
        }

        textInput {
            style {
                width = 150.px
                height = wrapContent
                fontSize = 12.px
                border = "0px solid #ffffff"
                marginStart = 8.px
                borderWidth = "0px"
                outline = "none"
            }
            placeholder = placeholderText
            element.onkeyup = {
                bouncer.buffer(this.text) {
                    onKeyPress(it)
                }
            }
        }
    }
}
