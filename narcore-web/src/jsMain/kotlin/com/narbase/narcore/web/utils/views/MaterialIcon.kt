@file:Suppress("unused")

package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.visit
import kotlinx.browser.document
import kotlinx.dom.addClass
import org.w3c.dom.HTMLElement

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

open class MaterialIcon(parent: View? = null, iconName: String) : View(parent) {
    override val element: HTMLElement = document.createElement("i") as HTMLElement
    override fun configureElement() {
        super.configureElement()
        this.element.addClass("material-icons")
        this.element.innerText = iconName
    }

    var iconName = iconName
        set(value) {
            field = value
            this.element.innerText = iconName
        }

    var size: MaterialSize? = null
        set(value) {
            value ?: return
            field = value
            this.element.style.fontSize = "${value}px"
        }

    companion object {
        const val md18: MaterialSize = 18
        const val md24: MaterialSize = 24
        const val md36: MaterialSize = 36
        const val md48: MaterialSize = 48
    }
}

typealias MaterialSize = Int

//enum class MaterialSizeE(val size: Int) {
//    Md18(18),
//    Md24(24),
//    Md36(36),
//    Md48(48),
//}

fun View?.materialIcon(iconName: String, block: (MaterialIcon.() -> Unit)? = null): MaterialIcon =
    MaterialIcon(this, iconName).visit(null, block ?: {})
