package com.narbase.narcore.web.network

open class GetItemsRequestDto<D>(
    val pageNo: Int = 0,
    val pageSize: Int = 10,
    val searchTerm: String = "",
    val data: D? = null
)

data class GetItemsResponseDto<D>(
    val list: Array<D>,
    val total: Int
)

open class ItemsRequestFactory<D> {
    open fun create(pageNo: Int, pageSize: Int, searchTerm: String) =
        GetItemsRequestDto<D>(pageNo, pageSize, searchTerm)
}


