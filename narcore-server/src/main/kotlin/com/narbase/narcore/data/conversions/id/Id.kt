package com.narbase.narcore.data.conversions.id

import com.narbase.narcore.dto.common.IdDto
import java.util.*

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/06.
 */


fun UUID.toDto() = IdDto(toString())
fun IdDto.toModel() = UUID.fromString(value)