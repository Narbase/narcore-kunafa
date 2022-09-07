@file:Suppress("unused")

package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.visit
import kotlinx.browser.document
import org.w3c.dom.HTMLParagraphElement

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2018] Narbase Technologies
 * All Rights Reserved.
 * Created by ${user}
 * On: ${Date}.
 */

open class Paragraph(parent: View? = null) : View(parent) {
    override val element: HTMLParagraphElement = document.createElement("p") as HTMLParagraphElement
}

fun View?.paragraph(block: (Paragraph.() -> Unit)? = null): Paragraph =
    Paragraph(this).visit(null, block ?: {})
