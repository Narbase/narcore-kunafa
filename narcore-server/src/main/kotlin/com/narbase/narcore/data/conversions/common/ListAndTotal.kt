package com.narbase.narcore.data.conversions.common

import com.narbase.narcore.data.models.utils.ListAndTotal

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/07.
 */

fun <Model, Dto> ListAndTotal<Model>.toDto(block: (Model) -> Dto) = ListAndTotal(list.map(block), total)