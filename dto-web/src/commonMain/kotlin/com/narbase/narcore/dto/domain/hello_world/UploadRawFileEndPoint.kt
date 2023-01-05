/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2022] Narbase Technologies
 * All Rights Reserved.
 * Created by shalaga44
 * On: 20/Dec/2022.
 */

package com.narbase.narcore.dto.domain.hello_world

import com.narbase.narcore.dto.models.RequestType
import com.narbase.narcore.router.EndPoint

object UploadRawFileEndPoint : EndPoint<RequestType.FormData, UploadRawFileEndPoint.Response>() {

    class Response(
        val file: FileDto,
    )
}

object UploadFileEndPoint : EndPoint<RequestType.FormData, UploadFileEndPoint.Response>() {

    class Response(
        val file: FileDto,
    )
}

class FileDto(
    val url: String,
    val fileName: String,
)