package com.narbase.narcore.data.conversions.id

import com.narbase.narcore.dto.common.IdDto
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


fun UUID.toDto() = IdDto(toString())
fun IdDto.toModel() = UUID.fromString(value)