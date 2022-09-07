package com.narbase.narcore.data.access.common

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/06.
 */
data class ListFilter<CustomFilter>(
    val pageNo: Long,
    val pageSize: Int,
    val searchTerm: String?,
    val customFilter: CustomFilter?,
)