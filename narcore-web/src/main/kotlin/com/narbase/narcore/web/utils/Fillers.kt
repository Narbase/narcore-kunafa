package com.narbase.narcore.web.utils

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.view
import com.narbase.kunafa.core.css.height
import com.narbase.kunafa.core.css.width
import com.narbase.kunafa.core.dimensions.Dimension
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.px

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2019/11/10.
 */

fun View?.horizontalFiller() = view { style { width = weightOf(1) } }

fun View?.horizontalFiller(dimension: Dimension) = view { style { width = dimension } }
fun View?.horizontalFiller(pixels: Int) = view { style { width = pixels.px } }

fun View?.verticalFiller() = view { style { height = weightOf(1) } }
fun View?.verticalFiller(dimension: Dimension) = view { style { height = dimension } }
fun View?.verticalFiller(pixels: Int) = view { style { height = pixels.px } }
