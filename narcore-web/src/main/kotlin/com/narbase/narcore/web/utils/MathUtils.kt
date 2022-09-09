package com.narbase.narcore.web.utils

import kotlin.math.round

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun Double.roundToTwoDigits() = (round((this + 0.00001) * 100) / 100)