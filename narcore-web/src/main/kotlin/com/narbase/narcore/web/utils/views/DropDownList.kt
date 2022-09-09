package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.Dimension
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
import com.narbase.narcore.web.storage.bidirectional
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.PopupZIndex
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import org.w3c.dom.events.EventListener

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class DropDownList<T>(
    private val name: String,
    private val controller: DropDownListViewController,
    private var items: Array<T>,
    private var itemToString: (T) -> String,
    private val defaultValue: T? = null,
    private val customRuleSet: RuleSet? = null,
    var onItemSelected: (T?) -> Unit,
    private val dropListStyle: DropDownListStyle,
    private val viewWidth: Dimension,
    private val slug: String? = null
) : Component() {

    var isDisabled = false
        set(value) {
            field = value
            if (value) {
                listIcon?.isVisible = false
                dropDownListAndSearchRootLayout.element.style.border = "unset"
                dropDownListView?.element?.style?.border = "unset"
            } else {
                listIcon?.isVisible = dropListStyle.showIcon
                dropDownListAndSearchRootLayout.element.style.border = "1px solid ${AppColors.borderColorHex}"
                dropDownListView?.element?.style?.border = "1px solid ${AppColors.borderColorHex}"
            }
        }

    private var selectedItem: T? = null
    private var dropDownTextView: TextView? = null
    private var dropDownListView: View? = null
    private var listLayoutTextView: LinearLayout? = null
    private var dropDownListLayout: LinearLayout? = null
    private val dropDownListAndSearchRootLayout by lazy {
        detached.verticalScrollLayout {
            style {
                position = "absolute"
                height = wrapContent
                maxHeight = 200.px
//                width = matchParent
                zIndex = 100
                border = "1px solid ${AppColors.borderColorHex}"
                boxShadow = "0px 6px 8px 1px rgba(0,0,0,0.1)"
                borderRadius = dropListStyle.borderRadius
                backgroundColor = Color.white
            }

            element.addClass("myClass")
            element.addClass("normalBar")

            dropDownListLayout = verticalLayout {
                style {
                    height = wrapContent
                    width = matchParent
                    zIndex = 100
                    borderRadius = 3.px
                }
                element.addClass("myClass")
            }

        }
    }
    private val hiddenBackground by lazy {
        detached.view {
            style {
                position = "absolute"
                top = 0.px
                bottom = 0.px
                left = 0.px
                right = 0.px
                zIndex = 99
                backgroundColor = Color.transparent
            }
            onClick = { controller.hideList() }
        }
    }


    private var listIcon: MaterialIcon? = null

    private val eventListener = EventListener { movePopupToListLayout() }

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        setupObservers()
        controller.onViewCreated()
        updateItems(items)
        document.body?.appendChild(dropDownListAndSearchRootLayout.element)
        document.body?.appendChild(hiddenBackground.element)
    }

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        setSelectedItem(defaultValue)
    }

    private fun setupObservers() {
        controller.isListShown.observe { isListShown ->
            isListShown ?: return@observe
            if (isListShown) {
                hiddenBackground.element.style.zIndex = PopupZIndex.getTopIndex().toString()
                dropDownListAndSearchRootLayout.element.style.zIndex = PopupZIndex.getTopIndex().toString()
                makeVisible(hiddenBackground, dropDownListAndSearchRootLayout)
                movePopupToListLayout()
                window.addEventListener("resize", eventListener)
            } else {
                PopupZIndex.restoreTopIndex()
                makeNotVisible(hiddenBackground, dropDownListAndSearchRootLayout)
                window.removeEventListener("resize", eventListener)
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
                    onClick = onClick@{ e ->
                        if (isDisabled) return@onClick asDynamic()
                        selectedItem = null
                        onItemSelected(null)
                        controller.setClear()
                        e.stopPropagation()
                    }
                }
            }
        }
    }

    private fun movePopupToListLayout() {
        val clientBottom = dropDownListView?.element?.getBoundingClientRect()?.bottom ?: 0.0
        val clientLeft = dropDownListView?.element?.getBoundingClientRect()?.left ?: 0.0
        val clientWidth = dropDownListView?.element?.getBoundingClientRect()?.width ?: 200.0

        val windowHeight = window.innerHeight
        if (windowHeight - clientBottom > 100) {
            dropDownListAndSearchRootLayout.element.style.top = "${clientBottom}px"
            dropDownListAndSearchRootLayout.element.style.bottom = "unset"
        } else {
            val clientTop = dropDownListView?.element?.getBoundingClientRect()?.top ?: 0.0
            dropDownListAndSearchRootLayout.element.style.bottom = "${windowHeight - clientTop}px"
            dropDownListAndSearchRootLayout.element.style.top = "unset"
        }
        dropDownListAndSearchRootLayout.element.style.width = "${clientWidth}px"
        dropDownListAndSearchRootLayout.element.style.left = "${clientLeft}px"
    }

    override fun View?.getView() = verticalLayout {
        slug?.let { id = "$slug-rootView" }
        style {
            width = viewWidth
        }

        if (customRuleSet != null)
            addRuleSet(customRuleSet) else style {
            backgroundColor = Color.white
        }

        dropDownListView = horizontalLayout {
            style {
                width = matchParent
                borderRadius = dropListStyle.borderRadius
                border = "1px solid ${AppColors.borderColorHex}"
                pointerCursor()
                alignItems = Alignment.Center
                padding = dropListStyle.padding
            }
            onClick = onClick@{
                if (isDisabled) return@onClick asDynamic()
                controller.showList()
            }

            dropDownTextView = textView {
                style {
                    width = weightOf(1)
                    fontSize = 14.px
                    color = AppColors.textDarkerGrey
                    padding = "2px 8px".dimen()
                    textAlign = bidirectional(TextAlign.Left, TextAlign.Right)
                    singleLine()
                }

                text = name
            }
            listIcon = materialIcon("keyboard_arrow_down") {
                style {
                    color = AppColors.textDarkGrey
                    margin = "0px 4px".dimen()
                }

                isVisible = dropListStyle.showIcon
                size = MaterialIcon.md18
            }
        }

        listLayoutTextView = verticalLayout {
            style {
                height = 0.px
                width = matchParent
                overflowY = "visible"
            }
        }

    }

    private val dropDownItemClass = classRuleSet {
        padding = 12.px
        color = AppColors.text
        backgroundColor = Color.white
        borderBottom = "1px solid ${AppColors.borderColorHex}"
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
            items.forEachIndexed { index, item ->
                layout.mount(
                    layout.verticalLayout {
                        slug?.let {
                            id = "$slug-listItem-$index"
                            element.addClass("$slug-listItem")
                        }
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
                                selectedItem = item
                                onItemSelected(item)
                                controller.hideList()
                                controller.setSelected()
                            }
                        }
                        if (index != items.lastIndex) {

                            view {
                                style {
                                    backgroundColor = AppColors.separatorLight
                                    height = 1.px
                                    width = matchParent
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    fun setSelectedItem(item: T?) {
        if (item != null) {
            selectedItem = item
            controller.setSelected()
            onItemSelected(item)
            dropDownTextView?.text = itemToString(item)
        }
    }

    fun getSelectedItem(): T? = selectedItem

    fun setSelectedItemBySearch(searchInItems: (Array<T>) -> T?) {
        setSelectedItem(searchInItems(items))
    }

    class DropDownListStyle(
        val showIcon: Boolean = true,
        val padding: Dimension = 4.px,
        val borderRadius: Dimension = 4.px
    )
}

class DropDownListViewController {
    val isListShown: Observable<Boolean> = Observable()
    val isClear = Observable<Boolean>()

    fun onViewCreated() {
        hideList()
        setClear()
    }

    fun showList() {
        isListShown.value = true
    }

    fun hideList() {
        isListShown.value = false
    }

    fun setSelected() {
        isClear.value = false
    }

    fun setClear() {
        isClear.value = true
    }
}

fun <T> View.setupDropDownList(
    name: String,
    list: Array<T>,
    itemToString: (T) -> String,
    defaultValue: T? = null,
    customRuleSet: RuleSet? = null,
    onItemSelected: (T?) -> Unit,
    listStyle: DropDownList.DropDownListStyle = DropDownList.DropDownListStyle(),
    viewWidth: Dimension = 300.px,
    slug: String? = null
): DropDownList<T> {

    return DropDownList(
        name,
        DropDownListViewController(),
        list,
        itemToString,
        defaultValue,
        customRuleSet,
        onItemSelected,
        listStyle,
        viewWidth,
        slug
    ).apply {
        this@setupDropDownList.mount(this)
    }
}
