package com.narbase.narcore.dto.common.network.crud

import com.narbase.narcore.dto.common.StringUUID
import kotlin.js.JsExport

@JsExport
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
        class Request(val id: StringUUID?)

        @Suppress("unused")
        inner class Response(val item: T)

    }

    class Delete {
        @Suppress("unused")
        class Request(val id: StringUUID?)

    }

}