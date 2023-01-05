package com.narbase.narcore.domain.user.crud

import com.narbase.narcore.router.CrudEndPoint
import kotlin.reflect.KClass


abstract class EndpointCrudController<DtoType : Any, ListRequestDtoType : Any>(
    val endPoints: List<CrudEndPoint<DtoType, ListRequestDtoType>>,
    dtoClass: KClass<DtoType>,
    ListRequestDtoClass: KClass<ListRequestDtoType>,
) : CrudController<DtoType, ListRequestDtoType>(dtoClass, ListRequestDtoClass)


