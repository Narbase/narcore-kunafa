package com.narbase.narcore.web.utils.imageUploader

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.networkCall
import com.narbase.narcore.web.utils.BasicUiState
import com.narbase.narcore.web.utils.views.ViewLoadingErrorComponent
import com.narbase.narcore.web.utils.views.pointerCursor
import org.w3c.files.File
import org.w3c.xhr.FormData

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


class SingleImageUploader(val selectedImage: File?, val onRemoveClicked: (toBeRemoved: SingleImageUploader) -> Unit) :
    Component() {
    val imageUrl
        get() = viewModel.imageUrl

    private val viewModel = SingleImageUploaderViewModel()
    private val uploadedImageView by lazy {
        detached.imageView {
            style {
                width = 90.px
                height = 90.px
                objectFit = "cover"
            }
        }
    }

    private val wrappedImage = ViewLoadingErrorComponent(uploadedImageView, onRetryClicked = {
        viewModel.uploadImage(selectedImage)
    })

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        viewModel.uploadImage(selectedImage)
        wrappedImage.bind(viewModel.imageUiState) {
            uploadedImageView.element.src = "${ServerCaller.BASE_URL}$imageUrl"

        }
    }

    override fun View?.getView() = verticalLayout {
        addRuleSet(Styles.rootStyle)
        wrappedImage.mountIn(this)
        textView {
            text = "X Remove"
            addRuleSet(Styles.removeButtonStyle)
            onClick = {
                onRemoveClicked(this@SingleImageUploader)
            }
        }
    }

    object Styles {
        val rootStyle = classRuleSet {
            height = 90.px
            width = 90.px
            margin = 4.px
            backgroundColor = AppColors.lightGreyBackground
            alignItems = Alignment.Center
            justifyContent = JustifyContent.Center
            position = "relative"
        }


        val removeButtonStyle = classRuleSet {
            position = "absolute"
            color = Color.transparent
            width = matchParent
            height = wrapContent
            bottom = 0.px
            left = 0.px
            right = 0.px
            textAlign = TextAlign.Center
            backgroundColor = Color.transparent
            fontSize = 14.px
            paddingTop = 4.px
            paddingBottom = 4.px
            pointerCursor()
            addCompoundRuleSet(rootStyle.selector.hover) {
                backgroundColor = AppColors.blueishGrey
                color = AppColors.redLight
            }
        }
    }
}

class SingleImageUploaderViewModel {
    val imageUiState = Observable<BasicUiState>()
    var imageUrl: String? = null

    fun uploadImage(selectedImage: File?) {
        selectedImage ?: return
        networkCall(
            before = { imageUiState.value = BasicUiState.Loading },
            onConnectionError = { imageUiState.value = BasicUiState.Error }
        ) {
            val formData = FormData()
            formData.append("image", selectedImage)
            val response = ServerCaller.uploadFile(formData)
            setHasImage(response.data.url)
        }
    }

    private fun setHasImage(imageUrl: String) {
        this.imageUrl = imageUrl
        imageUiState.value = BasicUiState.Loaded
    }
}

