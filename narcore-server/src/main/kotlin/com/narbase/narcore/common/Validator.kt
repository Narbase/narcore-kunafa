package com.narbase.narcore.common


/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

//fun validate(input: Any) {
//    val type = input.javaClass
//    type.declaredFields.forEach { classField ->
//        classField.isAccessible = true
//        val annotations = classField.annotations
//        annotations.forEach { annotation ->
//            when (annotation) {
//                is Required -> {
//                    val value = classField.get(input)
//                            ?: throw MissingArgumentException("Required field not found: ${classField.name}")
////                            try {
////                                classField.javaClass.cast(value)
////                            } catch (e: Exception){
////                                throw InvalidRequestException("Field: ${classField.name}: $value not of type: ${classField.class}")
////                            }
//                }
//                is NotEmptyString -> {
//                    if (classField.get(input).toString().isEmpty())
//                        throw MissingArgumentException("Not Empty String field: ${classField.name}")
//                }
//                is Range -> {
//                    val value = classField.get(input).toString()
//                    if (!(value.length in annotation.min..annotation.max))
//                        throw MissingArgumentException(
//                                "${classField.name} length should be in range: ${annotation.min} to ${annotation.max}")
//                }
//            }
//        }
//    }
//}

@Target(AnnotationTarget.FIELD)
annotation class Required

@Target(AnnotationTarget.FIELD)
annotation class NotEmptyString

@Target(AnnotationTarget.FIELD)
annotation class Range(val min: Int, val max: Int)

@Target(AnnotationTarget.FIELD)
annotation class Translate

@Target(AnnotationTarget.FIELD)
annotation class DoNotTranslate
