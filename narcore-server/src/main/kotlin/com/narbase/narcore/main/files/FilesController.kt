@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.narbase.narcore.main.files

import com.narbase.narcore.domain.user.files.CreateFileController
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

private const val pathParameterName = "static-content-path-parameter"
fun Route.filesWithThumbnailsGenerator(folder: String) = filesWithThumbnailsGenerator(File(folder))

fun Route.filesWithThumbnailsGenerator(folder: File) {
    val dir = staticRootFolder.combine(folder)
    get("{$pathParameterName...}") {
        getFile(dir)
    }
}

@Suppress("BlockingMethodInNonBlockingContext")
private suspend fun PipelineContext<Unit, ApplicationCall>.getFile(dir: File) {
    val parameters = call.parameters.getAll(pathParameterName) ?: return

    val relativePathWithThumbnailInfo = parameters.joinToString(File.separator)
    val thumbnailInfo = getThumbnailInfo(relativePathWithThumbnailInfo)

    if (thumbnailInfo == null) {
        val file = dir.combineSafe(relativePathWithThumbnailInfo)
        if (file.isFile) {
            call.respond(LocalFileContent(file))
            return
        }

    } else {
        val thumbnailFile = dir.combineSafe(thumbnailInfo.thumbnailPath)
        if (thumbnailFile.isFile) {
            call.respond(LocalFileContent(thumbnailFile))
            return
        }
        val file = dir.combineSafe(thumbnailInfo.originalFilePath)
        val image = ImageIO.read(file)
        val thumbnail = CreateFileController.resizeImage(
            image,
            BufferedImage.TYPE_INT_BGR,
            thumbnailInfo.width,
            thumbnailInfo.height
        )
        thumbnailFile.parentFile.mkdirs()
        ImageIO.write(thumbnail, "jpeg", thumbnailFile)
        call.respond(LocalFileContent(thumbnailFile))
    }

}

data class ThumbnailInfo(
    val originalFilePath: String,
    val thumbnailPath: String,
    val width: Int,
    val height: Int
)

/**
 * eg: filename-th.540.480
 */
private fun getThumbnailInfo(file: String?): ThumbnailInfo? {
    file ?: return null
    val thumbnailInfoString = file.split('-').lastOrNull() ?: return null
    val infoParts = thumbnailInfoString.split('.')
    if (infoParts.size != 3) return null
    if (infoParts.first() != "th") return null
    val width = infoParts[1].toIntOrNull() ?: return null
    val height = infoParts[2].toIntOrNull() ?: return null
    val acceptedWidth = acceptedThumbnailSizes.firstOrNull { it >= width } ?: acceptedThumbnailSizes.last()
    val acceptedHeight = acceptedThumbnailSizes.firstOrNull { it >= height } ?: acceptedThumbnailSizes.last()
    return ThumbnailInfo(
        file.removeSuffix("-$thumbnailInfoString"),
        "${file.removeSuffix("-$thumbnailInfoString")}-th.$acceptedWidth.$acceptedHeight.jpeg",
        acceptedWidth,
        acceptedHeight
    )


}

val acceptedThumbnailSizes = listOf(80, 240, 460, 680, 1024, 1920)

private fun File?.combine(file: File) = when {
    this == null -> file
    else -> resolve(file)
}
