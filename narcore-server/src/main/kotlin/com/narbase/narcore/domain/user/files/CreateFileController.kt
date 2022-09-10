package com.narbase.narcore.domain.user.files

import com.narbase.narcore.common.DataResponse
import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.common.exceptions.InvalidRequestException
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

class CreateFileController(
    private val shouldCompress: Boolean
) {

    class ResponseDto(
        val url: String,
        val fileName: String
    )

    suspend fun handle(call: ApplicationCall) {
        val clientData = call.principal<AuthorizedClientData>()
        val multipart = call.receiveMultipart()
        val parts = multipart.readAllParts()
        val imageTypeName = (parts.find { it is PartData.FormItem && it.name == "type" } as? PartData.FormItem)?.value
        val imageType = FileType.findType(imageTypeName)
        val image = parts.find { it is PartData.FileItem } as? PartData.FileItem ?: throw InvalidRequestException()
        val fileName = (parts.find { it is PartData.FormItem && it.name == "fileName" } as? PartData.FormItem)?.value
            ?.takeUnless { it.isBlank() } ?: image.originalFileName
        val directoryName = "upload-${System.currentTimeMillis()}-${clientData?.id}"
        val photoUrl = writeFile(image, directoryName, imageType, fileName)
        println(photoUrl)
        image.dispose()
        call.respond(DataResponse(ResponseDto(photoUrl, fileName ?: "")))
    }

    private fun writeFile(
        part: PartData.FileItem,
        directoryName: String,
        fileType: FileType,
        originalFileName: String?
    ): String {
        val out = Paths.get("./files/$directoryName/$originalFileName")
        val directoryPath = out.parent
        val filename = out.fileName
        val directory = directoryPath.toFile()
        if (!directory.exists()) {
            directory.mkdirs()
        }
        try {
            part.streamProvider().use { inputStream ->
                Files.copy(inputStream, out)
            }
            val image = ImageIO.read(out.toFile())
            if (image != null) {
                val type = BufferedImage.TYPE_3BYTE_BGR
                if (shouldCompress.not()) {
                    val imagePath = "$directoryPath/${originalFileName}"

                    val thumbnail = resizeImage(image, type, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
                    File("$imagePath-thumbnail.jpeg").parentFile.mkdirs()
                    ImageIO.write(thumbnail, "jpeg", File("$imagePath-thumbnail.jpeg"))
                    return "/files/$directoryName/${originalFileName}"
                }
//            val image = ImageIO.read(part.streamProvider())
                val convertedImg = BufferedImage(image.width, image.height, type)
                convertedImg.graphics.color = Color.white
                convertedImg.graphics.fillRect(0, 0, image.width, image.height)
                convertedImg.graphics.drawImage(image, 0, 0, null)
                convertedImg.graphics.dispose()

                val imagePath = "$directoryPath/${filename}.${fileType.width}x${fileType.height}_crop.jpeg"
                val newImage = resizeImage(convertedImg, type, fileType.width, fileType.height)
                ImageIO.write(newImage, "jpeg", File(imagePath))

                val thumbnail = resizeImage(convertedImg, type, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
                ImageIO.write(thumbnail, "jpeg", File("$imagePath-thumbnail.jpeg"))
                return "/files/$directoryName/${originalFileName}.${fileType.width}x${fileType.height}_crop.jpeg"
            } else {
                return "/files/$directoryName/${originalFileName}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }

    }

    companion object {

        fun resizeImage(originalImage: BufferedImage, type: Int, maxWidth: Int, maxHeight: Int): BufferedImage {

            val originalWidth = originalImage.width
            val originalHeight = originalImage.height

            var newWidth = originalWidth
            var newHeight = originalHeight

            if (originalWidth > maxWidth) {
                newWidth = maxWidth
                newHeight = (newWidth * originalHeight) / originalWidth
            }

            if (newHeight > maxHeight) {
                newHeight = maxHeight
                newWidth = (newHeight * originalWidth) / originalHeight
            }

            val resizedImage = BufferedImage(newWidth, newHeight, type)
            val g = resizedImage.createGraphics()
            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null)
            g.dispose()

            return resizedImage
        }

        const val THUMBNAIL_WIDTH = 200
        const val THUMBNAIL_HEIGHT = 200
    }

    enum class FileType(val type: String, val width: Int, val height: Int, val fullWidth: Int, val fullHeight: Int) {
        Image("image", 820, 500, 1920, 1170),
        ;

        companion object {
            fun findType(typeName: String?): FileType {
                return FileType.values().find { it.type == typeName } ?: Image
            }
        }
    }
}