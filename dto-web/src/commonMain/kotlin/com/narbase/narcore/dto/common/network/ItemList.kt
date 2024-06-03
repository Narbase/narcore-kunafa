package com.narbase.narcore.dto.common.network

import kotlin.js.JsExport

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
@JsExport
class ItemList<T>(
    val list: Array<T>,
    val total: Int
)