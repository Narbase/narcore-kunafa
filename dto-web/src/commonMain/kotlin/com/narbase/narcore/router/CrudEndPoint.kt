/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2022] Narbase Technologies
 * All Rights Reserved.
 * Created by shalaga44
 * On: 26/Dec/2022.
 */


package com.narbase.narcore.router

abstract class CrudEndPoint<DtoType : Any, out ListRequestDtoType : Any> : EndPoint<DtoType, ListRequestDtoType>() {
    lateinit var crudPrivileges: List<CrudEndpoints>
    fun applyContext(context: Routing.Context, crudPrivileges: List<CrudEndpoints>) {
        super.applyContext(context)
        this.crudPrivileges = crudPrivileges
    }

    override fun toString(): String {
        return "CrudEndPoint(crudPrivileges=$crudPrivileges, super=${super.toString()})"
    }

    enum class CrudEndpoints {
        AddEndpoint,
        UpdateEndpoint,
        DeleteEndpoint,
        DetailsEndpoint,
        ListEndpoint,
    }

    class CrudEndpointsBuilder() {
        val endPointList = mutableListOf<CrudEndpoints>()

        fun enableOnly(vararg endpoint: CrudEndpoints) {
            endPointList.clear()
            endPointList.addAll(endpoint)
        }

        fun enableExcept(vararg endpoints: CrudEndpoints) {
            endPointList.clear()
            endPointList.addAll(CrudEndpoints.values().filter { it !in endpoints })
        }
    }


}

fun <V : Any, D : Any> newSubEndPointWithDifferentType(path: String, oldEndPoint: EndPoint<*, *>): EndPoint<V, D> {
    val endPoint = object : EndPoint<V, D>() {}
    endPoint.path = "${oldEndPoint.path}$path"
    endPoint.privileges = oldEndPoint.privileges
    endPoint.authentication = oldEndPoint.authentication
    return endPoint
}

fun <V : Any, D : Any> newSubEndPoint(path: String, oldEndPoint: EndPoint<V, D>): EndPoint<V, D> {
    val endPoint = object : EndPoint<V, D>() {}
    endPoint.path = "${oldEndPoint.path}$path"
    endPoint.privileges = oldEndPoint.privileges
    endPoint.authentication = oldEndPoint.authentication
    return endPoint
}