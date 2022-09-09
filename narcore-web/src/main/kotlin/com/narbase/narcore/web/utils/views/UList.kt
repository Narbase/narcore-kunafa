@file:Suppress("unused")

package com.narbase.narcore.web.utils.views


import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.visit
import kotlinx.browser.document
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLUListElement

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

open class UList(parent: View? = null) : View(parent) {
    override val element: HTMLUListElement = document.createElement("ul") as HTMLUListElement
}

open class ListItem(parent: View? = null) : View(parent) {
    override val element: HTMLLIElement = document.createElement("li") as HTMLLIElement
}

fun View.ul(block: (UList.() -> Unit)? = null): UList = UList(this).visit(null, block ?: {})
fun View.li(block: (ListItem.() -> Unit)? = null): ListItem = ListItem(this).visit(null, block ?: {})
