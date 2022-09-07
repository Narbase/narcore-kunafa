package com.narbase.narcore.web.network.dto

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/04.
 */

class ItemList<T>(
    val list: Array<T>,
    val total: Int
)