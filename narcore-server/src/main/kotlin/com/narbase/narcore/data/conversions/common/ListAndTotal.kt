package com.narbase.narcore.data.conversions.common

import com.narbase.narcore.data.models.utils.ListAndTotal
import com.narbase.narcore.dto.common.utils.ListAndTotalDto

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun <Model, Dto> ListAndTotal<Model>.toDtoForCrud(block: (Model) -> Dto) = ListAndTotal(list.map(block), total)
inline fun <Model, reified Dto> ListAndTotal<Model>.toDto(block: (Model) -> Dto) = ListAndTotalDto(list.map(block).toTypedArray(), total)