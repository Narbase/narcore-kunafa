@file:Suppress("unused")

package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.visit
import kotlinx.browser.document
import org.w3c.dom.HTMLParagraphElement

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

open class Paragraph(parent: View? = null) : View(parent) {
    override val element: HTMLParagraphElement = document.createElement("p") as HTMLParagraphElement
}

fun View?.paragraph(block: (Paragraph.() -> Unit)? = null): Paragraph =
    Paragraph(this).visit(null, block ?: {})
