/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2022] Narbase Technologies
 * All Rights Reserved.
 * Created by shalaga44
 * On: 20/Dec/2022.
 */

package com.narbase.narcore.common


import com.narbase.narcore.dto.models.RequestType
import com.narbase.narcore.router.EndPoint
import kotlin.reflect.KClass


abstract class EndpointHandler<V : Any, out D : Any>(
    private val requestDtoClass: KClass<V>,
    val endPoint: EndPoint<V, D>,
) : Handler<V, D>(requestDtoClass)

abstract class BasicEndpointHandler<out D : Any>(
    val endPoint: EndPoint<RequestType.FormData, D>,
) : BasicHandler<D>()