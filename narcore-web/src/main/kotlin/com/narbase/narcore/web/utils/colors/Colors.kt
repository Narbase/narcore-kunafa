package com.narbase.narcore.web.utils.colors

import com.narbase.kunafa.core.drawable.Color

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2019/11/10.
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
