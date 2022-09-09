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
import com.narbase.narcore.web.events.EscapeClickedEvent
import com.narbase.narcore.web.network.*
import com.narbase.narcore.web.network.dto.ItemList
import com.narbase.narcore.web.storage.bidirectional
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.BasicUiState
import com.narbase.narcore.web.utils.DataResponse
import com.narbase.narcore.web.utils.DataResponse.Companion.BASIC_SUCCESS
import com.narbase.narcore.web.utils.PopupZIndex
import com.narbase.narcore.web.utils.eventbus.LifecycleSubscriber
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.dom.addClass
import org.w3c.dom.events.EventListener

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class RemoteDropDownList<T>(
    private val name: String,
    private val controller: RemoteDropDownListViewController<T>,
    private val defaultItem: T? = null,
    private val defaultItemBySearch: ((Array<T>) -> T?)? = null,
    private val rootStyle: RuleSet? = null,
    private var itemToString: (T) -> String,
    private val onItemSelected: (T?) -> Unit,
    private val showAutoComplete: Boolean = false,
    private val viewWidthFactory: (RuleSet.() -> Dimension),
    private val slug: String? = null
) : Component() {

    private var dropDownTextView: TextView? = null
    private var dropDownListView: View? = null
    private var bottomReferenceView: LinearLayout? = null
    private var dropDownListLayout: LinearLayout? = null
    private var isFirstTime = true
    private val dropDownListAndSearchRootLayout by lazy {
        detached.verticalScrollLayout {
            style {
                position = "absolute"
                height = wrapContent
                maxHeight = 200.px
                zIndex = 100
                border = "1px solid ${AppColors.borderColorHex}"
                boxShadow = "0px 6px 8px 1px rgba(0,0,0,0.1)"
                borderRadius = 4.px
                backgroundColor = Color.white
            }

            element.addClass("myClass")
            element.addClass("normalBar")

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
    private var searchText: TextInput? = null

    private val loading by lazy {
        detached.verticalLayout {
            style {
                width = matchParent
            }
            makeVisible(loadingIndicator())
        }
    }
    private val errorView by lazy {
        detached.textView {
            text = "Network Error. Retry"
            addRuleSet(dropDownItemClass)
            style {
                width = matchParent
            }
            onClick = {
                controller.getItems()
            }
        }
    }
    private val eventListener = EventListener { movePopupToListLayout() }

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        setupObservers()
        controller.onViewCreated()
        document.body?.appendChild(dropDownListAndSearchRootLayout.element)
        document.body?.appendChild(hiddenBackground.element)
        lifecycleOwner.bind(LifecycleSubscriber<EscapeClickedEvent> {
            controller.hideList()
        })


    }

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        setSelectedItem(defaultItem)
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
                if (showAutoComplete) {
                    searchText?.element?.focus()
                }
            } else {
                PopupZIndex.restoreTopIndex()
                makeNotVisible(hiddenBackground, dropDownListAndSearchRootLayout)
                window.removeEventListener("resize", eventListener)
                searchText?.text = ""
                searchDelayBouncer.onInputChanged(searchText?.text ?: "")
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
                onItemSelected(null)
            } else {
                listIcon?.apply {
                    iconName = "close"
                    addRuleSet(cancelIconClass)
                    onClick = { e ->
                        controller.setClear()
                        e.stopPropagation()
                    }
                }
            }
        }
        controller.loadingListState.observe { loadingState ->
            loadingState ?: return@observe
            dropDownListLayout?.removeChild(errorView)
            dropDownListLayout?.removeChild(loading)
            when (loadingState) {
                BasicUiState.Loading -> {
                    dropDownListLayout?.mount(loading)
                }

                BasicUiState.Loaded -> {
                    updateItems()
                    if (isFirstTime) {
                        setSelectedItem(defaultItemBySearch?.invoke(controller.items))
                        isFirstTime = false
                    }
                }

                BasicUiState.Error -> {
                    dropDownListLayout?.mount(errorView)
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

    fun setSelectedItem(item: T?) {
        if (item != null) {
            controller.setSelected()
            onItemSelected(item)
            dropDownTextView?.text = itemToString(item)
        }
    }

    fun setSelectedItemBySearch(searchInItems: (Array<T>) -> T?) {
        setSelectedItem(searchInItems(controller.items))
    }


    override fun View?.getView() = verticalLayout {
        slug?.let { id = "$slug-rootView" }
        style {
            width = viewWidthFactory()
        }

        if (rootStyle == null) addRuleSet(Styles.dropDownRootStyle) else addRuleSet(rootStyle)

        dropDownListView = horizontalLayout {
            style {
                width = matchParent
                borderRadius = 4.px
                border = "1px solid ${AppColors.borderColorHex}"
                pointerCursor()
                alignItems = Alignment.Center
            }

            onClick = { controller.showList() }

            dropDownTextView = textView {
                style {
                    width = weightOf(1)
                    fontSize = 14.px
                    color = AppColors.textDarkerGrey
                    padding = "6px 12px".dimen()
                    textAlign = bidirectional(TextAlign.Left, TextAlign.Right)
                    singleLine()
                }

                text = name
            }

            listIcon = materialIcon("keyboard_arrow_down") {
                style {
                    color = AppColors.textDarkGrey
                    margin = "4px 8px".dimen()
                }

                size = MaterialIcon.md18
            }
        }

        bottomReferenceView = verticalLayout {
            style {
                height = 0.px
                width = matchParent
                overflowY = "visible"

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
                        placeholder = "Search".localized()
                    }
                    placeholder = "Search".localized()
                    /*
                                        onEnterPressed {
                                            searchDelayBouncer.onInputChanged(this.text)
                                        }
                    */
                    element.oninput = {
                        searchDelayBouncer.onInputChanged(this.text)
                    }
                }
            }
        }
    }

    private fun doSearch(text: String) {
        controller.searchTerm = text
        refreshList()
    }


    private val dropDownItemClass = classRuleSet {
        padding = 12.px
        color = AppColors.text
        backgroundColor = Color.white
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

    fun refreshList() = controller.getInitialItems()

    private fun updateItems() {
        val layout = dropDownListLayout ?: return
        layout.clearAllChildren()
        if (controller.items.isEmpty()) {
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
            controller.items.forEachIndexed { index, item ->
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
                                controller.hideList()
                                controller.setSelected()
                                onItemSelected(item)
                            }
                        }
                        if (index != controller.items.lastIndex) {
                            view {
                                style {
                                    backgroundColor = Color(255, 255, 255, 0.1)
                                    height = 1.px
                                    width = matchParent
                                }
                            }
                        }
                    }
                )
            }
        }

        dropDownListAndSearchRootLayout.element.onscroll = {
            dropDownListAndSearchRootLayout.element.apply {
                if (scrollTop + offsetHeight >= scrollHeight) {
                    controller.getNextItems()
                }
            }
        }
    }

    object Styles {
        val dropDownRootStyle = classRuleSet {
            backgroundColor = Color.white
        }
    }

    fun clear() {
        controller.setClear()
    }

    fun dismiss() {
        controller.hideList()
    }
}

open class RemoteDropDownListViewController<T>(
    private val endPoint: String,
    private val requestFactory: ItemsRequestFactory<*>,
    private val isMock: Boolean,
    private val mockList: Array<T>
) {
    val isListShown: Observable<Boolean> = Observable()
    val isClear = Observable<Boolean>()
    var items: Array<T> = arrayOf()

    var page: Int = 0
    var size: Int = 30

    var loadingListState = Observable<BasicUiState>()
    var hasReachedEnd = false

    var searchTerm: String? = null

    fun onViewCreated() {
        getInitialItems()
        hideList()
        setClear()
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

    internal var getItemsJob: Job? = null
    open fun getItems() {
        if (isMock) {
            items = mockList
            loadingListState.value = BasicUiState.Loaded
        } else {
            getItemsJob?.cancel()
            getItemsJob = networkCall(
                before = { loadingListState.value = BasicUiState.Loading },
                onConnectionError = { loadingListState.value = BasicUiState.Error }
            ) {
                val responseDto =
                    ServerCaller.getItems<T>(endPoint, requestFactory.create(page, size, searchTerm ?: ""))

                when {
                    responseDto.status == "$BASIC_SUCCESS" -> {
                        val newItems = responseDto.data.list
                        if (newItems.size < size)
                            hasReachedEnd = true
                        items += newItems
                        loadingListState.value = BasicUiState.Loaded
                    }

                    responseDto.status == DataResponse.INVALID_REQUEST -> loadingListState.value = BasicUiState.Loaded
                    else -> loadingListState.value = BasicUiState.Error
                }
            }
        }
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

class CustomDropDownListViewController<T>(
    private val getList: (pageNo: Int, pageSize: Int) -> Deferred<ItemList<T>>,
    private val isMock: Boolean,
    private val mockList: Array<T>
) : RemoteDropDownListViewController<T>("", ItemsRequestFactory<Unit>(), isMock, mockList) {

    override fun getItems() {
        if (isMock) {
            items = mockList
            loadingListState.value = BasicUiState.Loaded
        } else {
            getItemsJob?.cancel()
            getItemsJob = networkCall(
                before = { loadingListState.value = BasicUiState.Loading },
                onConnectionError = { loadingListState.value = BasicUiState.Error }
            ) {
                val response = getList(page, size).await()

                val newItems = response.list
                if (newItems.size < size)
                    hasReachedEnd = true
                items += newItems
                loadingListState.value = BasicUiState.Loaded
            }
        }
    }
}


fun <T> View.setupRemoteDropDownList(
    name: String,
    endPoint: String,
    itemToString: (T) -> String,
    onItemSelected: (T?) -> Unit,
    itemsRequestFactory: ItemsRequestFactory<*> = ItemsRequestFactory<Unit>(),
    rootStyle: RuleSet? = null,
    defaultItem: T? = null,
    showAutoComplete: Boolean = false,
    viewWidth: Dimension = 300.px,
    viewWidthFactory: (RuleSet.() -> Dimension)? = null,
    isMock: Boolean = false,
    mockList: Array<T> = arrayOf(),
    slug: String? = null,
    defaultItemBySearch: ((Array<T>) -> T?)? = null,
): RemoteDropDownList<T> {

    return RemoteDropDownList(
        name,
        RemoteDropDownListViewController(endPoint, itemsRequestFactory, isMock, mockList),
        defaultItem,
        defaultItemBySearch,
        rootStyle,
        itemToString,
        onItemSelected,
        showAutoComplete,
        viewWidthFactory ?: { viewWidth },
        slug
    ).apply {
        this@setupRemoteDropDownList.mount(this)
    }
}

fun <T> View.setupRemoteDropDownList(
    name: String,
    getList: (pageNo: Int, pageSize: Int) -> Deferred<ItemList<T>>,
    itemToString: (T) -> String,
    onItemSelected: (T?) -> Unit,
    rootStyle: RuleSet? = null,
    defaultItem: T? = null,
    showAutoComplete: Boolean = false,
    viewWidth: Dimension = 300.px,
    viewWidthFactory: (RuleSet.() -> Dimension)? = null,
    isMock: Boolean = false,
    mockList: Array<T> = arrayOf(),
    slug: String? = null,
    defaultItemBySearch: ((Array<T>) -> T?)? = null
): RemoteDropDownList<T> {

    return RemoteDropDownList(
        name,
        CustomDropDownListViewController(getList, isMock, mockList),
        defaultItem,
        defaultItemBySearch,
        rootStyle,
        itemToString,
        onItemSelected,
        showAutoComplete,
        viewWidthFactory ?: { viewWidth },
        slug
    ).apply {
        this@setupRemoteDropDownList.mount(this)
    }
}
