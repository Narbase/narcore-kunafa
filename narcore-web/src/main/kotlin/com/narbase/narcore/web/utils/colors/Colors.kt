package com.narbase.narcore.web.utils.colors

import com.narbase.kunafa.core.drawable.Color

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

private val blackToWhiteStep = 25.5

fun gray(factor: Double) = Color(
    (blackToWhiteStep * factor * 10).toInt(),
    (blackToWhiteStep * factor * 10).toInt(),
    (blackToWhiteStep * factor * 10).toInt()
)

fun whiteTransparent(factor: Double) = Color(255, 255, 255, factor)

fun blackTransparent(factor: Double) = Color(0, 0, 0, factor)

fun Color.lighter(factor: Double): Color {
    val red = toDto().slice(0..1).toInt(16)
    val green = toDto().slice(2..3).toInt(16)
    val blue = toDto().slice(4..5).toInt(16)
    val lighterColorRed = ((255 - red) * factor + red).toInt()
    val lighterColorGreen = ((255 - green) * factor + green).toInt()
    val lighterColorBlue = ((255 - blue) * factor + blue).toInt()
    return Color(lighterColorRed, lighterColorGreen, lighterColorBlue)
}
