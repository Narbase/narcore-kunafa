package com.narbase.narcore.data.access.common

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
data class ListFilter<CustomFilter>(
    val pageNo: Long,
    val pageSize: Int,
    val searchTerm: String?,
    val customFilter: CustomFilter?,
)