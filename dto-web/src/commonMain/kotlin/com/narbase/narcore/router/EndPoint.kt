/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

package com.narbase.narcore.router

import com.narbase.narcore.dto.models.roles.Privilege

abstract class EndPoint<Request : Any, out Response : Any> {
    lateinit var path: String
    lateinit var privileges: List<Privilege>
    lateinit var authentication: List<Authentication>

    open fun applyContext(context: Routing.Context) {
        val endPoint = this@EndPoint
        endPoint.path = context.route
        endPoint.privileges = context.privileges.toList()
        endPoint.authentication = context.authentications.toList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EndPoint<*, *>

        if (path != other.path) return false
        if (privileges != other.privileges) return false
        if (authentication != other.authentication) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + privileges.hashCode()
        result = 31 * result + authentication.hashCode()
        return result
    }

    override fun toString(): String {
        return "EndPoint(path='$path', privileges=$privileges, authentication=$authentication)"
    }


}