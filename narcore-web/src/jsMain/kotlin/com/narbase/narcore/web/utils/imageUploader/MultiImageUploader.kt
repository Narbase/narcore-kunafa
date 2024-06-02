package com.narbase.narcore.web.utils.imageUploader

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.network.makeNotVisible
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.utils.views.pointerCursor
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.get

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class MultiImageUploader : Component() {
    private var uploadInput: Input? = null

    private val imageUploaders = mutableListOf<SingleImageUploader>()

    private val plusButton by lazy {
        detached.verticalLayout {
            style {
                height = 90.px
                width = 90.px
                margin = 4.px
                backgroundColor = AppColors.lightGreyBackground
                alignItems = Alignment.Center
                justifyContent = JustifyContent.Center
                pointerCursor()
                hover {
                    backgroundColor = AppColors.blueishGrey
                }
            }
            textView {
                text = "+"
                style {
                    fontSize = 42.px
                    color = Color.white
                }
            }
            onClick = {
                uploadInput?.element?.click()
            }

        }
    }

    private var imagesParentView: LinearLayout? = null

    override fun View?.getView() = horizontalScrollLayout {
        style {
            width = matchParent
            height = wrapContent
            backgroundColor = AppColors.mainDark
            marginTop = 18.px
        }
        horizontalLayout {
            style {
                height = matchParent
                width = wrapContent
                padding = 4.px
            }
            uploadInput = input {
                isVisible = false
                type = "file"
                element.onchange = { event ->
                    val selectedImage = (event.target as HTMLInputElement).files?.get(0)
                    onNewImageSelected(selectedImage)
                }
            }
            imagesParentView = horizontalLayout {
                style {
                    width = wrapContent
                    height = matchParent
                    borderWidth = "0px"
                }
            }

            mount(plusButton)
        }
    }

    private fun onRemoveClicked(toBeRemoved: SingleImageUploader) {
        imageUploaders.remove(toBeRemoved)
        imagesParentView?.unMount(toBeRemoved)
        updatePlusButtonState()
    }

    private fun onNewImageSelected(selectedImage: File?) {
        selectedImage ?: return
        val singleImageUploader = SingleImageUploader(selectedImage, this::onRemoveClicked)
        imageUploaders.add(singleImageUploader)
        imagesParentView?.mount(singleImageUploader)
        updatePlusButtonState()
    }

    private fun updatePlusButtonState() {
        if (imageUploaders.size >= 5) {
            makeNotVisible(plusButton)
        } else {
            makeVisible(plusButton)
        }
    }

    fun getUrls(): List<String> {
        return imageUploaders.mapNotNull { it.imageUrl }
    }
}


class MultiImageUploaderViewModel
