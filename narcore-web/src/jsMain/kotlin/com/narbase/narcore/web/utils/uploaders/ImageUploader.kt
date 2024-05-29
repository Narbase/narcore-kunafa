package com.narbase.narcore.web.utils.uploaders

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.makeNotVisible
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.network.networkCall
import com.narbase.narcore.web.utils.views.loadingIndicator
import com.narbase.narcore.web.utils.views.materialIcon
import com.narbase.narcore.web.utils.views.pointerCursor


import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.get
import org.w3c.xhr.FormData

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class ImageUploader(val defaultImageUrl: String? = null, private val imageStyle: (RuleSet.() -> Unit)?) : Component() {

    var imageUrl
        get() = uploaderViewController.imageUrl
        set(value) {
            if (value != null) {
                uploaderViewController.setHasImage(value)
            } else {
                uploaderViewController.clearImage()
            }
        }

    val uploaderViewController = ImageUploaderViewController()

    private var uploadButton: LinearLayout? = null
    private var uploadInput: Input? = null
    private var progressBar: ImageView? = null

    private var uploadedView: LinearLayout? = null
    private var uploadedImage: ImageView? = null


    private fun onImageStateChanged(state: ImageUploaderViewController.UploadedImageState) {
        makeNotVisible(uploadButton, progressBar, uploadedView)
        when (state) {
            ImageUploaderViewController.UploadedImageState.Clear -> makeVisible(uploadButton)
            ImageUploaderViewController.UploadedImageState.Uploading -> makeVisible(progressBar)
            ImageUploaderViewController.UploadedImageState.Uploaded -> {
                makeVisible(uploadedView)
                uploadedImage?.element?.src = "${ServerCaller.BASE_URL}$imageUrl"
            }
        }
    }

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        uploaderViewController.imageUiState.observe {
            onImageStateChanged(it ?: ImageUploaderViewController.UploadedImageState.Clear)
        }
    }

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        if (defaultImageUrl != null) {
            uploaderViewController.setHasImage(defaultImageUrl)
        }
    }

    override fun View?.getView() =
        verticalLayout {
            id = "Image Uploader root view"
            style {
                width = wrapContent
                alignItems = Alignment.Center
            }

            uploadButton = horizontalLayout {
                style {
                    width = wrapContent
                    padding = "2px 20px".dimen()
                    border = "1px solid ${AppColors.borderColorHex}"
                    borderRadius = 18.px
                    backgroundColor = AppColors.narcoreColor
                    color = Color.white
                    alignItems = Alignment.Center
                    opacity = 1.0
                    pointerCursor()
                }
                materialIcon("add") {
                    style {
                        marginEnd = 8.px
                    }
                }
                textView {
                    text = "Click to add image"
                }
                uploadInput = input {
                    isVisible = false
                    type = "file"
                    element.onchange = { event ->
                        val selectedImage = (event.target as HTMLInputElement).files?.get(0)
                        uploaderViewController.uploadImage(selectedImage)
                    }
                }

                onClick = {
                    uploadInput?.element?.click()
                }

            }
            progressBar = loadingIndicator()
            uploadedView = verticalLayout {
                isVisible = false

                uploadedImage = imageView {
                    if (imageStyle == null) style {
                        width = 100.px
                        height = 100.px
                        objectFit = "cover"
                        border = "1px solid rgba(0, 0, 0, 0.23)"
                    }
                    else style { imageStyle.invoke(this) }
                }

                horizontalLayout {
                    style {
                        paddingStart = 8.px
                        paddingEnd = 8.px
                        paddingBottom = 2.px
                        paddingTop = 2.px
                        backgroundColor = Color("f9f9f9")
                        borderRadius = 18.px
                        marginTop = 8.px
                        pointerCursor()
                    }

                    imageView {
                        style {
                            width = 18.px
                            height = 18.px
                        }

                        element.src = "/public/img/delete.png"
                    }

                    textView {
                        style {
                            alignSelf = Alignment.Center
                            opacity = 0.7
                            color = Color.red
                            fontSize = 13.px
                            marginStart = 6.px
                        }

                        text = "Delete"
                    }
                    onClick = {
                        uploaderViewController.clearImage()
                    }
                }


                /*
                                horizontalLayout {
                                    style {
                                        marginTop = 8.px
                                        width = wrapContent
                                        alignSelf = Alignment.Center
                                        alignItems = Alignment.Center
                                      //  color = AppColors.redLight
                                        padding = st("8px 16px")
                                        borderRadius = 4.px
                                        hover {
                                       //     backgroundColor = AppColors.backgroundColorLight
                                            cursor = "pointer"
                                        }
                                    }
                                    materialIcon("delete") {
                                        style {
                                            marginEnd = 8.px
                                        }
                                    }
                                    textView {
                                        text = "Remove image"
                                    }
                                    onClick = {
                                        uploaderViewController.clearImage()
                                    }

                                }
                */

            }
        }


}

class Input(parent: LinearLayout? = null) : LinearLayout(parent) {
    override val element: HTMLInputElement = document.createElement("input") as HTMLInputElement
    var type
        get() = element.type
        set(value) {
            element.type = value
        }
}

fun LinearLayout.input(block: Input.() -> Unit): Input = Input(this).visit(null, block)


class ImageUploaderViewController {
    val imageUiState = Observable<UploadedImageState>()
    var imageUrl: String? = null

    init {
        imageUiState.value = UploadedImageState.Clear
    }

    fun uploadImage(selectedImage: File?) {
        selectedImage ?: return
        networkCall(
            before = { imageUiState.value = UploadedImageState.Uploading },
            onConnectionError = { imageUiState.value = UploadedImageState.Clear }
        ) {
            val formData = FormData()
            formData.append("image", selectedImage)
            val response = ServerCaller.uploadFile(formData)
            setHasImage(response.data.url)
        }
    }

    fun clearImage() {
        imageUrl = null
        imageUiState.value = UploadedImageState.Clear
    }

    fun setHasImage(imageUrl: String) {
        this.imageUrl = imageUrl
        imageUiState.value = UploadedImageState.Uploaded
    }

    enum class UploadedImageState {
        Clear, Uploading, Uploaded
    }
}

@JsExport
class UploadFileResponseDto(
    val url: String,
    val fileName: String
)

fun View?.imageUploader(defaultImageUrl: String? = null, imageStyle: (RuleSet.() -> Unit)? = null): ImageUploader {

    return ImageUploader(defaultImageUrl, imageStyle).apply {
        this@imageUploader?.mount(this)
    }
}
