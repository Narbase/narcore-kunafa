package com.narbase.narcore.web.utils

import kotlin.math.round

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/09/10.
 */

fun Double.roundToTwoDigits() = (round((this + 0.00001) * 100) / 100)