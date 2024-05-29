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
import com.narbase.narcore.web.network.networkCall
import com.narbase.narcore.web.utils.BasicUiState
import com.narbase.narcore.web.utils.views.pointerCursor
import com.narbase.narcore.web.utils.views.withLoadingAndError
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList
import org.w3c.files.File
import org.w3c.xhr.FormData

class DirectoryUploader(val directoryOnly: Boolean, private val onSave: () -> Unit) : Component() {

    private val uploaderViewController = DirectoryUploaderViewController()
    val files
        get() = uploaderViewController.files

    private var uploadButton: LinearLayout? = null
    private var progressBar: ImageView? = null

    private var componentRootView: View? = null

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        componentRootView?.withLoadingAndError(uploaderViewController.uploadingFileUiState,
            onRetryClicked = {
                uploaderViewController.uploadAllFiles()
            },
            onLoaded = {
                onSave()
                uploaderViewController.uploadingFileUiState.value = null
            })
    }


    var fileInput: TextInput? = null
    override fun View?.getView() = verticalLayout {
        id = "File Uploader root view"
        style {
            width = wrapContent
            padding = 8.px
        }

        componentRootView = textView {
            style {
                padding = "4px 18px".dimen()
                color = Color.white
                fontSize = AppFontSizes.largeButtonText
                borderRadius = 50.px
                backgroundColor = AppColors.narcoreColor
                pointerCursor()
                focus {
                    outline = "none"
                }
            }
            onClick = {
                fileInput?.element?.click()
            }
            text = if (directoryOnly) "Add folder" else "Add 1 or more files"
        }


        fileInput = textInput {
            isVisible = false
            style {
                fontSize = AppFontSizes.smallButtonSize
            }
            type = "file"
            if (directoryOnly) {
                element.asDynamic().webkitdirectory = true
            }
            element.multiple = true

            element.onchange = { event ->
                val files = (event.target as HTMLInputElement).files
                uploaderViewController.files =
                    files?.asList()?.map { DirectoryUploaderViewController.FileState(it, it.name) }
                        ?: listOf()
                uploaderViewController.uploadAllFiles()
            }
        }


    }

    fun View?.verticalSeparator(separatorHeight: Int = 1) = view {
        style {
            width = matchParent
            height = separatorHeight.px
            backgroundColor = Color(AppColors.borderColorHex)
        }
    }
}

class DirectoryUploaderViewController {

    class FileState(
        val file: File,
        var fileName: String,
        var fileUrl: String? = null,
        val fileUiState: Observable<UploadedFileState> = Observable<UploadedFileState>().apply {
            value = UploadedFileState.Clear
        }

    )

    var files = listOf<FileState>()
    val uploadingFileUiState = Observable<BasicUiState>()

    fun uploadAllFiles() {
        uploadingFileUiState.value = BasicUiState.Loading
        networkCall(
            onConnectionError = { uploadingFileUiState.value = BasicUiState.Error }
        ) {
            files.forEach { selectedFile ->
                val formData = FormData()
                formData.append("file", selectedFile.file)
                val response = ServerCaller.uploadRawFile(formData)
//                selectedFile.fileUiState.value = UploadedFileState.Uploaded
                selectedFile.fileUrl = response.data.url
                selectedFile.fileName = response.data.fileName
            }
            uploadingFileUiState.value = BasicUiState.Loaded
        }
    }

    fun uploadAllFilesFromDragAndDrop() {
        uploadingFileUiState.value = BasicUiState.Loading
        networkCall(
            onConnectionError = { uploadingFileUiState.value = BasicUiState.Error }
        ) {
            files.forEach { selectedFile ->
                val formData = FormData()
                formData.append("file", selectedFile.file)
                formData.append("fileName", selectedFile.fileName)//Todo: figure out why I need to have this difference
                val response = ServerCaller.uploadRawFile(formData)
//                selectedFile.fileUiState.value = UploadedFileState.Uploaded
                selectedFile.fileUrl = response.data.url
                selectedFile.fileName = response.data.fileName
            }
            uploadingFileUiState.value = BasicUiState.Loaded
        }
    }

    enum class UploadedFileState {
        Clear, Uploading, Uploaded
    }
}

fun LinearLayout.directoryUploader(directoryOnly: Boolean, onSave: () -> Unit): DirectoryUploader {

    return DirectoryUploader(directoryOnly, onSave)
}
