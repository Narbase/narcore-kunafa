package com.narbase.narcore.web.utils.views

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
import com.narbase.narcore.web.network.ItemsRequestFactory
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.network.networkCall
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.BasicUiState
import com.narbase.narcore.web.utils.DataResponse.Companion.BASIC_SUCCESS

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class RemoteCheckBoxList<T>(
    private val name: String,
    private val controller: RemoteCheckBoxListViewController<T>,
    private var itemToString: (T) -> String,
    private val onSelection: (T) -> Unit
) : Component() {

    private var listLayoutTextView: LinearLayout? = null
    private var checkBoxListLayout: LinearLayout? = null
    private var dropDownAndButtonLayout: LinearLayout? = null

    private val loading by lazy {
        detached.verticalLayout {
            style {
                width = matchParent
                backgroundColor = Color.white
            }
            makeVisible(loadingIndicator())
        }
    }
    private val errorView by lazy {
        detached.textView {
            text = "Network Error. Retry"
            addRuleSet(checkBoxTextStyle)
            style {
                width = matchParent
            }
            onClick = {
                controller.getItems()
            }
        }
    }

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        setupObservers()
        controller.onViewCreated()
    }

    private fun setupObservers() {
        controller.loadingListState.observe { loadingState ->
            loadingState ?: return@observe
            checkBoxListLayout?.removeChild(errorView)
            checkBoxListLayout?.removeChild(loading)
            when (loadingState) {
                BasicUiState.Loading -> {
                    checkBoxListLayout?.mount(loading)
                }

                BasicUiState.Loaded -> {
                    updateItems()
                }

                BasicUiState.Error -> {
                    checkBoxListLayout?.mount(errorView)
                }
            }
        }
    }

    override fun View?.getView() = verticalLayout {
        style {
            width = matchParent
            height = matchParent
        }

        checkBoxListLayout = verticalLayout {
            style {
                width = matchParent
            }

        }
    }

    private val checkBoxTextStyle = classRuleSet {
        padding = 8.px
        fontSize = 14.px
        cursor = "pointer"
    }

    fun refreshList() = controller.getItems()

    val checkedBackground = classRuleSet {
        backgroundColor = AppColors.narcoreColor
    }
    val unCheckedBackground = classRuleSet {
        backgroundColor = Color.white
    }
    val hoveredBackground = classRuleSet {
        backgroundColor = Color("eee")
    }

    private fun updateItems() {
        val layout = checkBoxListLayout ?: return
        layout.clearAllChildren()
        if (controller.items.isNullOrEmpty()) {
            layout.mount(
                layout.textView {
                    style {
                        width = matchParent
                        height = wrapContent
                    }
                    addRuleSet(checkBoxTextStyle)
                    text = "No Items found".localized()
                    onClick = { }
                }
            )
        } else {
            controller.items.forEach { selectableItem ->
                layout.mount(
                    layout.verticalLayout {
                        val checkMark by lazy {
                            verticalLayout {
                                style {
                                    width = matchParent
                                    height = matchParent
                                    border = "1px solid ${Color.white}"
                                }
                                if (selectableItem.isSelected) {
                                    addRuleSet(checkedBackground)
                                } else {
                                    addRuleSet(unCheckedBackground)
                                }
                            }
                        }
                        val customCheckbox by lazy {
                            detached.verticalLayout {
                                style {
                                    height = 12.px
                                    width = 12.px
                                    alignItems = Alignment.Center
                                    justifyContent = JustifyContent.Center
                                    border = "thin solid ${AppColors.narcoreColor}"
                                }
                                mount(checkMark)
                                if (selectableItem.isSelected) {
                                    checkMark.addRuleSet(checkedBackground)
                                } else {
                                    checkMark.addRuleSet(unCheckedBackground)
                                }
                            }
                        }


                        style {
                            width = matchParent
                            height = wrapContent
                        }
                        val checkbox by lazy {
                            detached.checkbox {
                                onChange = {
                                    selectableItem.isSelected = selectableItem.isSelected.not()
                                    onSelection(selectableItem.item)
                                    if (isChecked) {
                                        checkMark.removeRuleSet(unCheckedBackground)
                                        checkMark.addRuleSet(checkedBackground)
                                    } else {
                                        checkMark.removeRuleSet(checkedBackground)
                                        checkMark.addRuleSet(unCheckedBackground)
                                    }
                                }
                                isChecked = selectableItem.isSelected
                                style {
                                    height = 0.px
                                    width = 0.px
                                    display = "none"
                                }
                            }

                        }
                        horizontalLayout {
                            mount(customCheckbox)

                            style {
                                width = matchParent
                                height = wrapContent
                                alignItems = Alignment.Baseline
                                padding = "0px 0px".dimen()
                            }
                            mount(checkbox)

                            textView {
                                style {
                                    width = weightOf(1)
                                    height = wrapContent
                                }
                                addRuleSet(checkBoxTextStyle)
                                text = itemToString(selectableItem.item)
                            }
                            onClick = { checkbox.element.click() }
                        }
                    }
                )
            }
        }

        layout.element.onscroll = {
            layout.element.apply {
                if (scrollTop + offsetHeight >= scrollHeight) {
                    controller.getNextItems()
                }
            }
        }
    }
}

class RemoteCheckBoxListViewController<T>(
    private val endPoint: String,
    val requestFactory: ItemsRequestFactory<*> = ItemsRequestFactory<Unit>(),
    var items: Array<SelectableItem<T>> = arrayOf()
) {

    class SelectableItem<T>(val item: T, var isSelected: Boolean = false)

    var page: Int = 0
    var size: Int = 20

    var loadingListState = Observable<BasicUiState>()
    var hasReachedEnd = false

    init {
        getInitialItems()

    }

    fun getInitialItems() {
        hasReachedEnd = false
        page = 0
        items = arrayOf()
        getItems()
    }

    fun getNextItems() {
        if (loadingListState.value != BasicUiState.Loaded) return
        if (hasReachedEnd) return
        page += 1
        getItems()
    }

    fun onViewCreated() {
    }

    fun getItems() {

        networkCall(before = { loadingListState.value = BasicUiState.Loading },
            onConnectionError = { loadingListState.value = BasicUiState.Error }) {
            val responseDto = ServerCaller.getItems<T>(endPoint, requestFactory.create(page, size, ""))
            if (responseDto.status == "$BASIC_SUCCESS") {
                val newItems = responseDto.data.list.map { SelectableItem(it) }.toTypedArray()
                if (newItems.size < size)
                    hasReachedEnd = true
                items += newItems
                loadingListState.value = BasicUiState.Loaded
            } else
                loadingListState.value = BasicUiState.Error
        }
    }

}

fun <T> LinearLayout.setupRemoteCheckBoxList(
    name: String,
    endPoint: String,
    itemToString: (T) -> String,
    onSelection: (T) -> Unit,
    itemsRequestFactory: ItemsRequestFactory<*> = ItemsRequestFactory<Unit>()
): RemoteCheckBoxList<T> {

    return RemoteCheckBoxList(
        name,
        RemoteCheckBoxListViewController(endPoint, itemsRequestFactory),
        itemToString,
        onSelection
    ).apply {
        this@setupRemoteCheckBoxList.mount(this)
    }
}
