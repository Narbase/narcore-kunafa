package com.narbase.narcore.web.network.crud

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/02/04.
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
