package com.narbase.narcore.web.utils.uploaders

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.common.AppFontSizes
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.basicNetworkCall
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.utils.BasicUiState
import com.narbase.narcore.web.utils.views.pointerCursor
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.get
import org.w3c.xhr.FormData

class SimpleFileUploader : Component() {

    var fileUrl: String? = null
    var fileName: String? = null
    //  get() = uploaderViewController.fileUrl

    private var fileTextInput: TextInput? = null
    val viewModel = SimpleFileUploaderViewController()

    private var uploadButton: LinearLayout? = null
    private var progressBar: ImageView? = null

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        viewModel.fileUiState.observe {
            onFileStateChanged(it)
        }
    }


    private fun onFileStateChanged(state: BasicUiState?) {
        when (state) {
            null -> makeVisible(uploadButton)
            BasicUiState.Error -> makeVisible(uploadButton)
            BasicUiState.Loading -> makeVisible(progressBar)
            BasicUiState.Loaded -> {
                fileUrl = viewModel.fileUrl
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

            verticalLayout {
                style {
                    width = matchParent
                    padding = 18.px
                    borderRadius = 6.px
                    border = "1px dashed ${AppColors.borderColorHex}"
                    marginBottom = 18.px
                }

                fileTextInput = textInput {
                    style {
                        fontSize = AppFontSizes.smallButtonSize
                        pointerCursor()
                    }
                    type = "file"
                    element.accept = "image/*"
                    element.onchange = { event ->
                        val selectedFile = (event.target as HTMLInputElement).files?.get(0)
                        selectedFile?.name?.let {
                            fileName = it
                        }
                        viewModel.uploadFile(selectedFile)
                    }
                }
            }
        }

    fun showSelectionDialog() {
        fileTextInput?.element?.click()
    }

    fun View?.verticalSeparator(separatorHeight: Int = 1) = view {
        style {
            width = matchParent
            height = separatorHeight.px
            backgroundColor = Color(AppColors.borderColorHex)
        }
    }
}

class SimpleFileUploaderViewController {
    val fileUiState = Observable<BasicUiState>()
    var fileUrl: String? = null

    init {
        fileUiState.value = null
    }

    fun uploadFile(selectedFile: File?) {
        selectedFile ?: return
        fileUiState.value = BasicUiState.Loading
        basicNetworkCall(fileUiState) {
            val formData = FormData()
            formData.append("file", selectedFile)
            val response = ServerCaller.uploadRawFile(formData)
            fileUrl = response.data.url
        }
    }
}


fun LinearLayout.simpleFileUploader(): SimpleFileUploader {

    return SimpleFileUploader()
}
