package com.narbase.narcore.web.network.crud

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


object CrudDto {

    open class GetList {

        @Suppress("unused")
        open class Request<D>(
            val pageNo: Int = 0,
            val pageSize: Int? = null,
            val searchTerm: String = "",
            val data: D? = null
        )

        @Suppress("unused")
        open inner class Response<T>(
            val list: Array<T>,
            val total: Int
        )

    }

    class GetItem<T> {

        @Suppress("unused")
        class Request(val id: String?)

        @Suppress("unused")
        inner class Response(val item: T)

    }

    class Delete {
        @Suppress("unused")
        class Request(val id: String?)

    }

}
