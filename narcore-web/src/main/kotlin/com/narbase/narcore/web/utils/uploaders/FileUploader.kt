package com.narbase.narcore.web.utils.uploaders

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.common.AppFontSizes
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.network.networkCall
import com.narbase.narcore.web.utils.views.pointerCursor
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.get
import org.w3c.xhr.FormData

class FileUploader(private val onSave: () -> Unit) : Component() {

    var fileUrl: String? = null
    var fileName: String? = null
    //  get() = uploaderViewController.fileUrl

    private val uploaderViewController = FileUploaderViewController()

    private var uploadButton: LinearLayout? = null
    private var titleInput: TextInput? = null
    private var progressBar: ImageView? = null

    private var uploadedView: LinearLayout? = null
    private var uploadedImage: ImageView? = null

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        uploaderViewController.fileUiState.observe {
            onFileStateChanged(it ?: return@observe)
        }
    }


    private fun onFileStateChanged(state: FileUploaderViewController.UploadedFileState) {
        when (state) {
            FileUploaderViewController.UploadedFileState.Clear -> makeVisible(uploadButton)
            FileUploaderViewController.UploadedFileState.Uploading -> makeVisible(progressBar)
            FileUploaderViewController.UploadedFileState.Uploaded -> {
                fileUrl = uploaderViewController.fileUrl
                makeVisible(uploadButton)
//                makeVisible(uploadedView)
//                uploadedImage?.element?.src = "${ServerCaller.BASE_URL}$fileUrl"
            }
        }
    }

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {

//        if (defaultfileUrl != null) {
//            uploaderViewController.setHasFile(defaultfileUrl)
        //   }
    }

    override fun View?.getView() =
        verticalLayout {
            id = "File Uploader root view"
            style {
                width = wrapContent
                padding = 8.px
                // alignItems = Alignment.Center
            }

            textView {
                style {
                    fontSize = AppFontSizes.normalText
                    marginBottom = 10.px
                }
                text = "Add 1 or more file (no folders)"
            }

            verticalSeparator()


            textView {
                style {
                    fontSize = AppFontSizes.smallText
                    marginTop = 12.px
                    color = AppColors.narcoreColor
                }

                text = "Title"
            }

            titleInput = textInput {
                style {
                    width = matchParent
                    fontSize = AppFontSizes.smallText
                    padding = 8.px
                    backgroundColor = Color.white
                    borderRadius = 6.px
                    border = "1px solid ${AppColors.borderColorHex}"
                    marginTop = 8.px
                    marginBottom = 12.px
                    color = Color.black
                    focus {
                        outline = "none"
                    }
                }
                element.oninput = {
                    fileName = this.text
                    1
                }
            }

            verticalLayout {
                style {
                    width = matchParent
                    padding = 18.px
                    borderRadius = 6.px
                    border = "1px dashed ${AppColors.borderColorHex}"
                    marginBottom = 18.px
                }

                textInput {
                    style {
                        // color = Color.white
                        fontSize = AppFontSizes.smallButtonSize
                        // borderRadius = 50.px
                        // backgroundColor = AppColors.narcoreColor
                        //pointerCursor()
                    }
                    type = "file"
                    element.onchange = { event ->
                        val selectedFile = (event.target as HTMLInputElement).files?.get(0)
                        selectedFile?.name?.let {
                            titleInput?.text = it
                            fileName = it
                        }
                        uploaderViewController.uploadFile(selectedFile)
                    }
                }
            }

            textView {
                style {
                    padding = "4px 18px".dimen()
                    color = Color.white
                    fontSize = AppFontSizes.smallButtonSize
                    borderRadius = 50.px
                    backgroundColor = AppColors.narcoreColor
                    pointerCursor()
                }
                text = "Save"
                onClick = { onSave() }
            }

//                uploadButton = horizontalLayout {
//                    style {
//                        width = wrapContent
//                    }
//                    textView {
//                        style {
//                            padding = st("4px 18px")
//                            color = Color.white
//                            fontSize = AppFontSizes.largeButtonText
//                            fontWeight = "bold"
//                            borderRadius = 50.px
//                            backgroundColor = AppColors.narcoreColor
//                            pointerCursor()
//                        }
//
//                        text = "Add files"
//                    }
//                    uploadInput = input {
//                        isVisible = false
//                        type = "file"
//                        element.onchange = { event ->
//                            val selectedFile = (event.target as HTMLInputElement).files?.get(0)
//                            uploaderViewController.uploadFile(selectedFile)
//                        }
//                    }
//
//                    onClick = {
//                        uploadInput?.element?.click()
//                    }
//
//                }
            //   progressBar = loadingIndicator()
//                uploadedView = verticalLayout {
//                    isVisible = false
//
//                    uploadedImage = imageView {
//                        style {
//                            width = 100.px
//                            height = 100.px
//                            objectFit = "cover"
//                            border = "1px solid rgba(0, 0, 0, 0.23)"
//                        }
//                    }
//                }
        }

    fun View?.verticalSeparator(separatorHeight: Int = 1) = view {
        style {
            width = matchParent
            height = separatorHeight.px
            backgroundColor = Color(AppColors.borderColorHex)
        }
    }
}

class FileUploaderViewController {
    val fileUiState = Observable<UploadedFileState>()
    var fileUrl: String? = null

    init {
        fileUiState.value = UploadedFileState.Clear
    }

    fun uploadFile(selectedFile: File?) {
        selectedFile ?: return
        networkCall(
            before = { fileUiState.value = UploadedFileState.Uploading },
            onConnectionError = { fileUiState.value = UploadedFileState.Clear }
        ) {
            val formData = FormData()
            formData.append("file", selectedFile)
            val response = ServerCaller.uploadRawFile(formData)
            setHasFile(response.data.url)
        }
    }

    fun setHasFile(url: String) {
        fileUrl = url
        fileUiState.value = UploadedFileState.Uploaded
    }

    enum class UploadedFileState {
        Clear, Uploading, Uploaded
    }
}

fun LinearLayout.fileUploader(onSave: () -> Unit): FileUploader {

    return FileUploader(onSave)
}
