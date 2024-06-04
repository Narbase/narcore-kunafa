/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2024] Narbase Technologies
 * All Rights Reserved.
 * Created by shalaga44
 * On: 28/Feb/2024.
 */

package com.narbase.narcore.dto.common

actual typealias KmmLong = Double

actual fun kmmLongOf(value: Long): Double {
    return value.toDouble()
}