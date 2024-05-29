package com.narbase.narcore.web.utils.views.customViews

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.network.makeNotVisible
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.translations.localized


import com.narbase.narcore.web.utils.scrollable.ScrollableView
import com.narbase.narcore.web.utils.views.DelayBouncer
import com.narbase.narcore.web.utils.views.MaterialIcon
import com.narbase.narcore.web.utils.views.materialIcon
import kotlinx.dom.addClass

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class CustomDropDownList<T>(
    private val name: String,
    private val controller: DropDownListViewController,
    private var items: Array<T>,
    private var itemToString: (T) -> String,
    private val onItemSelected: (T?) -> Unit,
    private val defaultItem: T? = null,
    private val showAutoComplete: Boolean = false

) : Component() {

    private var dropDownTextView: TextView? = null
    private var scrollableList: ScrollableView? = null
    private var listLayoutTextView: LinearLayout? = null
    private var dropDownListLayout: LinearLayout? = null
    private var listIcon: MaterialIcon? = null
    private var dropDownListAndSearchRootLayout: LinearLayout? = null
    private var searchText: TextInput? = null


    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        setupObservers()
        controller.onViewCreated()
        updateItems(items)
    }

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        super.onViewMounted(lifecycleOwner)
        setDefaultItem(defaultItem)
    }

    fun setDefaultItem(item: T?) {
        if (item != null) {
            controller.setSelected()
            onItemSelected(item)
            dropDownTextView?.text = itemToString(item)
        }
    }

    private fun setupObservers() {
        controller.isListShown.observe { isListShown ->
            isListShown ?: return@observe
            if (isListShown) {
                makeVisible(listLayoutTextView)
                if (showAutoComplete) {
                    searchText?.element?.focus()
                }
            } else {
                makeNotVisible(listLayoutTextView)
            }
        }
        controller.isClear.observe { isClear ->
            isClear ?: return@observe
            if (isClear) {
                dropDownTextView?.text = name
                listIcon?.apply {
                    removeRuleSet(cancelIconClass)
                    iconName = "keyboard_arrow_down"
                    onClick = { }
                }
            } else {
                listIcon?.apply {
                    iconName = "close"
                    addRuleSet(cancelIconClass)
                    onClick = { e ->
                        onItemSelected(null)
                        controller.setClear()
                        e.stopPropagation()
                    }
                }
            }
        }
    }

    override fun View?.getView() = verticalLayout {

        style {
            width = 300.px
            marginEnd = 16.px
            fontSize = 14.px
            // padding = st("2px 4px")
            borderRadius = 8.px
            border = "1px solid ${AppColors.borderColorHex}"
            color = AppColors.textDarkerGrey
        }

        horizontalLayout {
            style {
                width = matchParent
                cursor = "pointer"
                alignItems = Alignment.Center
                //                marginTop = 4.px
//                borderRadius = "3px"
//                border = "0.5px solid #959595"
            }
            onClick = { controller.showList() }
            dropDownTextView = textView {
                style {
                    text = name
                    width = weightOf(1)
                    padding = "6px 12px".dimen()
                    fontSize = 14.px
                }
            }
            listIcon = materialIcon("keyboard_arrow_down") {
                size = MaterialIcon.md24
                style {
                    color = AppColors.textDarkGrey
                    marginStart = 8.px
                    marginEnd = 8.px
                }
            }
        }

        listLayoutTextView = verticalLayout {
            style {
                height = 0.px
                width = matchParent
                // overflowY = "visible"
            }

            dropDownListAndSearchRootLayout = verticalScrollLayout {
                style {
                    height = wrapContent
                    maxHeight = 200.px
                    width = matchParent
                    zIndex = 100
                    border = "1px solid ${AppColors.borderColorHex}"

                    borderRadius = 12.px
                    backgroundColor = Color.white
                }
                element.addClass("myClass")

                if (showAutoComplete) {
                    listSearchView()
                }

                dropDownListLayout = verticalLayout {
                    style {
                        height = wrapContent
                        width = matchParent
                        zIndex = 100
                        borderRadius = 3.px
                    }
                }

            }



            view {
                style {
                    position = "absolute"
                    top = 0.px
                    bottom = 0.px
                    left = 0.px
                    right = 0.px
                    zIndex = 99
                    backgroundColor = Color.transparent
                    onClick = { controller.hideList() }
                }
            }
        }

    }

    private val searchDelayBouncer = DelayBouncer(400) { text: String ->
        doSearch(text)
    }

    private fun LinearLayout.listSearchView() {
        horizontalLayout {
            style {
                width = matchParent
                padding = 8.px
                alignSelf = Alignment.Center
                alignItems = Alignment.Center
            }
            horizontalLayout {
                style {
                    width = matchParent
                    height = 30.px
                    alignSelf = Alignment.Center
                    alignItems = Alignment.Center
                    padding = 8.px
                    border = "1px solid ${AppColors.borderColorHex}"
                    borderRadius = 50.px
                }
                materialIcon("search") {
                    style {
                        color = AppColors.textDarkGrey
                        fontSize = 20.px
                        margin = 5.px
                    }
                    onClick = {
                        searchDelayBouncer.onInputChanged(searchText?.text ?: "")
                    }
                }
                searchText = textInput {
                    style {
                        width = weightOf(1, 100.px)
                        backgroundColor = Color.white
                        border = "none"
                        fontSize = 14.px
                        color = AppColors.textDarkGrey
                        focus {
                            outline = "none"
                        }
                    }
                    placeholder = "Search .."
                    /*
                                        onEnterPressed {
                                            searchDelayBouncer.onInputChanged(this.text)
                                        }
                    */
                    element.onkeyup = {
                        searchDelayBouncer.onInputChanged(this.text)
                    }
                }
            }
        }
    }

    private fun doSearch(text: String) {
        controller.searchTerm = text
        if (text.isNullOrBlank()) updateItems(items)
        val originalItems = items
        var filteredItems = items.filter {
            itemToString(it).capitalize().matches(text.capitalize())
        }.toTypedArray()
        updateItems(filteredItems)
        items = originalItems
    }


    private val dropDownItemClass = classRuleSet {
        padding = "6px 12px".dimen()
        color = AppColors.text
        backgroundColor = Color.white
        // borderBottom = "thin solid ${Color("ccc")}"
        fontSize = 14.px
        cursor = "pointer"
        hover {
            backgroundColor = AppColors.separatorLight
        }
    }

    private var cancelIconClass = classRuleSet {
        hover {
            color = AppColors.redLight
        }
    }

    fun updateItems(newItems: Array<T>? = null) {
        newItems?.let {
            items = newItems
        }
        val layout = dropDownListLayout ?: return
        layout.clearAllChildren()
        if (items.isNullOrEmpty()) {
            layout.mount(
                layout.textView {
                    style {
                        width = matchParent
                        height = wrapContent
                    }
                    addRuleSet(dropDownItemClass)
                    text = "No Items found".localized()
                    onClick = { controller.hideList() }
                }
            )
        } else {
            items.forEach { item ->
                layout.mount(
                    layout.verticalLayout {
                        style {
                            width = matchParent
                            height = wrapContent
                        }


                        textView {
                            style {
                                width = matchParent
                                height = wrapContent
                            }
                            addRuleSet(dropDownItemClass)
                            text = itemToString(item)
                            onClick = {
                                dropDownTextView?.text = itemToString(item)
                                onItemSelected(item)
                                controller.hideList()
                                controller.setSelected()
                            }
                        }
//                            view {
//                                style {
//                                    backgroundColor = AppColors.separatorLight
//                                    height = 0.5.px
//                                    width = matchParent
//                                }
//                            }
                    }
                )
            }


        }
    }
}

class DropDownListViewController {
    val isListShown: Observable<Boolean> = Observable()
    val isClear = Observable<Boolean>()
    var searchTerm: String? = null

    fun onViewCreated() {
        hideList()
        setClear()
    }

    fun showList() {
        isListShown.value = true
    }

    fun hideList() {
        searchTerm = null
        isListShown.value = false
    }

    fun setSelected() {
        isClear.value = false
    }

    fun setClear() {
        isClear.value = true
    }
}

fun <T> View.setupCustomDropDownList(
    name: String,
    list: Array<T>,
    itemToString: (T) -> String,
    onItemSelected: (T?) -> Unit,
    defaultItem: T? = null,
    showAutoComplete: Boolean = false
): CustomDropDownList<T> {

    return CustomDropDownList(
        name,
        DropDownListViewController(),
        list,
        itemToString,
        onItemSelected,
        defaultItem,
        showAutoComplete
    ).apply {
        this@setupCustomDropDownList.mount(this)
    }
}
