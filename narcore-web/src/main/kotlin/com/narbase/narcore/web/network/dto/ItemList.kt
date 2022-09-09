package com.narbase.narcore.web.network.dto

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

class ItemList<T>(
    val list: Array<T>,
    val total: Int
)