package com.narbase.narcore.dto.models


sealed interface RequestType {
    object FormData : RequestType
}