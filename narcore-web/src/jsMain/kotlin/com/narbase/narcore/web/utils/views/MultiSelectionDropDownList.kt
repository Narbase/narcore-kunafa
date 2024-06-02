package com.narbase.narcore.web.utils.views


import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.Dimension
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.percent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.common.AppFontSizes
import com.narbase.narcore.web.events.EscapeClickedEvent
import com.narbase.narcore.web.network.makeNotVisible
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.storage.bidirectional
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.BasicUiState
import com.narbase.narcore.web.utils.PopupZIndex
import com.narbase.narcore.web.utils.eventbus.LifecycleSubscriber
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import org.w3c.dom.events.EventListener


class RemoteMultiSelectionDropDownList<T>(
    private val name: String,
    private val viewModel: MultiSelectionDropDownListViewModel<T>,
    private val defaultItems: List<T>? = null,
    private val rootStyle: RuleSet? = null,
    private var itemToString: (T) -> String,
    private var itemToId: (T) -> String,
    private val onSelectedItemsUpdatedCallback: (RemoteMultiSelectionDropDownList<T>.(List<T>) -> Unit)?,
    private val showAutoComplete: Boolean = false,
    private val viewWidthFactory: (RuleSet.() -> Dimension),
    private val slug: String? = null,
    private val isDisabledInitial: Boolean
) : Component() {

    val selectedItems: List<T>
        get() = viewModel.selectedItems

    private var dropDownTextView: TextView? = null
    private var dropDownHeader: LinearLayout? = null
    private var dropDownListView: View? = null
    private var bottomReferenceView: LinearLayout? = null
    private var dropDownListLayout: LinearLayout? = null

    private val selectedItemsViews = mutableMapOf<String, LinearLayout>()
    val isDisabled = Observable<Boolean>().apply {
        value = isDisabledInitial
    }


    private val dropDownListAndSearchRootLayout by lazy {
        detached.verticalScrollLayout {
            style {
                position = "absolute"
                height = wrapContent
                maxHeight = 200.px
                zIndex = PopupZIndex.getTopIndex()
                border = "1px solid ${AppColors.borderColorHex}"
                boxShadow = "0px 6px 8px 1px rgba(0,0,0,0.1)"
                borderRadius = 4.px
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
                zIndex = PopupZIndex.getTopIndex()
                backgroundColor = Color.transparent
            }
            onClick = { viewModel.hideList() }
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
                viewModel.getItems()
            }
        }
    }
    private val eventListener = EventListener { movePopupToListLayout() }

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
//        defaultItems?.let { viewModel.setSelectedItems(it.toTypedArray()) }
        viewModel.onViewCreated(defaultItems?.toTypedArray())
        setupObservers()
        document.body?.appendChild(dropDownListAndSearchRootLayout.element)
        document.body?.appendChild(hiddenBackground.element)
        lifecycleOwner.bind(LifecycleSubscriber<EscapeClickedEvent> {
            viewModel.hideList()
        })


    }


    private fun setupObservers() {
        viewModel.isListShown.observe { isListShown ->
            isListShown ?: return@observe
            if (isListShown) {
                updateItems()
                makeVisible(hiddenBackground, dropDownListAndSearchRootLayout)
                movePopupToListLayout()
                window.addEventListener("resize", eventListener)
                if (showAutoComplete) {
                    searchText?.element?.focus()
                    searchDelayBouncer.onInputChanged(searchText?.text ?: "")
                }
            } else {
                makeNotVisible(hiddenBackground, dropDownListAndSearchRootLayout)
                window.removeEventListener("resize", eventListener)
                searchText?.text = ""
            }
        }
//        viewModel.isClear.observe { isClear ->
//            console.log("isClear: $isClear")
//            isClear ?: return@observe
//            if (isClear) {
//                clearDropListView()
//            } else {
//                showCrossIcon()
//            }
//        }
        viewModel.selectedItemsUpdated.observe {
            onSelectedItemsUpdated(viewModel.selectedItems)
            onSelectedItemsUpdatedCallback?.invoke(this, viewModel.selectedItems)
        }

        viewModel.loadingListState.observe { loadingState ->
            loadingState ?: return@observe
            dropDownListLayout?.removeChild(errorView)
            dropDownListLayout?.removeChild(loading)
            when (loadingState) {
                BasicUiState.Loading -> {
                    dropDownListLayout?.mount(loading)
                }

                BasicUiState.Loaded -> {
                    updateItems()
                }

                BasicUiState.Error -> {
                    dropDownListLayout?.mount(errorView)
                }
            }
        }
    }

    private fun clearDropListView() {
        dropDownHeader?.clearAllChildren()
        dropDownHeader?.apply { dropDownTextView = listTitle() }
        listIcon?.apply {
            removeRuleSet(cancelIconClass)
            iconName = "keyboard_arrow_down"
            onClick = { }
        }
    }

    private fun onSelectedItemsUpdated(selectedItemsIds: MutableList<T>) {

        val viewsIds = selectedItemsViews.keys
        selectedItemsIds.filterNot { it.id in viewsIds }.forEach {
            dropDownHeader?.apply { selectedItemView(it) }
        }

        val updatedIds = selectedItemsIds.map { it.id }
        val toRemove = selectedItemsViews.filterNot { it.key in updatedIds }
        toRemove.forEach { makeNotVisible(it.value) }
        toRemove.keys.forEach { selectedItemsViews.remove(it) }
        if (selectedItemsViews.isEmpty()) {
            clearDropListView()
        } else {
            makeNotVisible(dropDownTextView)
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
            isDisabled.observe {
                if (it == true) {
                    element.style.cursor = "not-allowed"
                    onClick = {
                    }

                } else {
                    element.style.cursor = "pointer"
                    onClick = {
                        viewModel.showList()
                    }

                }
            }


            dropDownHeader = horizontalLayout {
                style {
                    width = weightOf(1)
                    height = wrapContent
                    padding = "6px 12px".dimen()
                    flexWrap = "wrap"
                }
                dropDownTextView = listTitle()
            }


            listIcon = materialIcon("keyboard_arrow_down") {
                style {
                    color = AppColors.textDarkGrey
                    margin = "4px 8px".dimen()
                    alignSelf = Alignment.Start
                }
                isDisabled.observe { isVisible = it != true }

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

    private fun View.listTitle() = textView {
        style {
            width = matchParent
            fontSize = 14.px
            color = AppColors.textDarkerGrey
            textAlign = bidirectional(TextAlign.Left, TextAlign.Right)
            singleLine()
        }

        text = name
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
        viewModel.searchTerm = text
        refreshList()
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

    fun refreshList() = viewModel.getInitialItems()

    private fun updateItems() {
        val layout = dropDownListLayout ?: return
        layout.clearAllChildren()
        if (viewModel.items.isEmpty()) {
            layout.mount(
                layout.textView {
                    style {
                        width = matchParent
                        height = wrapContent
                    }
                    addRuleSet(dropDownItemClass)
                    text = "No Items found".localized()
                    onClick = { viewModel.hideList() }
                }
            )
        } else {
            viewModel.items.forEachIndexed { _, selectableItem ->
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
                                    selectableItem.isSelected = isChecked
                                    viewModel.setItemSelected(selectableItem.item, selectableItem.isSelected)
                                    if (isChecked) {
                                        checkMark.removeRuleSet(unCheckedBackground)
                                        checkMark.addRuleSet(checkedBackground)
                                    } else {
                                        checkMark.removeRuleSet(checkedBackground)
                                        checkMark.addRuleSet(unCheckedBackground)
                                    }
                                    movePopupToListLayout()
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
                            addRuleSet(dropDownItemClass)
                            style {
                                width = matchParent
                                height = wrapContent
                                alignItems = Alignment.Baseline
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

        dropDownListAndSearchRootLayout.element.onscroll = {
            dropDownListAndSearchRootLayout.element.apply {
                if (scrollTop + offsetHeight >= scrollHeight) {
                    viewModel.getNextItems()
                }
            }
        }
    }

    private fun LinearLayout.selectedItemView(item: T): View {
        return horizontalLayout {
            style {
                alignItems = Alignment.Center
                border = "1px solid ${AppColors.borderColor}"
                borderRadius = 4.px
                padding = 4.px
                marginEnd = 6.px
                backgroundColor = AppColors.lightBackground
                color = AppColors.textDarkGrey
                cursor = "auto"
                marginBottom = 2.px
                marginTop = 2.px
                maxWidth = 100.percent
                flexShrink = "1"
            }

            onClick = { it.stopPropagation() }

            textView {
                style {
                    flexShrink = "1"
                    fontSize = AppFontSizes.smallerText
                    whiteSpace = "nowrap"
                    overflow = "hidden"
                    textOverflow = "ellipsis"
                }
                text = itemToString(item)
            }

            materialIcon("close") {
                style {
                    fontSize = AppFontSizes.smallText
                    marginStart = 6.px
                    pointerCursor()
                    hover {
                        color = AppColors.redLight
                    }
                }
                isDisabled.observe { isVisible = it != true }

                onClick = {
//                    itemCheckBox.element.click()

                    viewModel.setItemSelected(item, false)

                    /*makeNotVisible(this@horizontalLayout)
                    selectedItemsViews.remove(itemToString(item))
                    if (selectedItemsViews.isEmpty()) viewModel.setClear()*/
                }
            }

            selectedItemsViews[itemToId(item)] = this
        }
    }


    object Styles {
        val dropDownRootStyle = classRuleSet {
            backgroundColor = Color.white
        }
    }

    companion object {
        val checkedBackground = classRuleSet {
            backgroundColor = AppColors.narcoreColor
        }
        val unCheckedBackground = classRuleSet {
            backgroundColor = Color.white
        }

        private val checkBoxTextStyle = classRuleSet {
            padding = 8.px
            fontSize = 14.px
            cursor = "pointer"
        }
    }

    fun clear() {
        viewModel.setClear()
    }

    val T.id get() = itemToId(this)

}

class MultiSelectionDropDownListViewModel<T>(
    private val isMock: Boolean = false,
    private val mockList: Array<SelectableItem<T>> = arrayOf(),
    val itemToId: (T) -> String,
    private val getRemoteItems: MultiSelectionDropDownListViewModel<T>.(page: Int, searchTerm: String) -> Unit
) {
    val isListShown: Observable<Boolean> = Observable()

    //    val isClear = Observable<Boolean>()
    val selectedItemsUpdated = Observable<Boolean>()
    var items: Array<SelectableItem<T>> = arrayOf()
    val selectedItems = mutableListOf<T>()

    var page: Int = 0
    var size: Int = 30

    var loadingListState = Observable<BasicUiState>()
    var hasReachedEnd = false

    var searchTerm: String? = null

    fun onViewCreated(defaultItems: Array<T>?) {
        getInitialItems()
        hideList()
        if (defaultItems == null) setClear() else setSelectedItems(defaultItems)
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

    fun getItems() {
        if (isMock) {
            items = mockList
            loadingListState.value = BasicUiState.Loaded
        } else {
            getRemoteItems(page, searchTerm ?: "")
            /* getItemsJob?.cancel()
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
                         items += newItems.map { SelectableItem(it, true) }
                         loadingListState.value = BasicUiState.Loaded
                     }
                     responseDto.status == DataResponse.INVALID_REQUEST -> loadingListState.value = BasicUiState.Loaded
                     else -> loadingListState.value = BasicUiState.Error
                 }
             }*/
        }
    }

    fun showList() {
        isListShown.value = true
    }

    fun hideList() {
        searchTerm = null
        isListShown.value = false
    }

    fun setClear() {
        setSelectedItems(arrayOf())
    }

    fun setLoading() {
        loadingListState.value = BasicUiState.Loading
    }

    fun appendItems(newItems: Array<T>, endOfList: Boolean) {
        val selected = selectedItems.map { it.id }
        val newItemsAsSelectableItems = newItems.map {
            SelectableItem(it, selected.contains(it.id))
        }.toTypedArray()
        items = arrayOf(*items, *newItemsAsSelectableItems)
        hasReachedEnd = endOfList
        loadingListState.value = BasicUiState.Loaded
    }

    fun setItems(newItems: Array<T>) {
        val selected = selectedItems.map { it.id }
        items = newItems.map {
            SelectableItem(it, selected.contains(it.id))
        }.toTypedArray()
        loadingListState.value = BasicUiState.Loaded
    }

    fun setItemSelected(item: T, isSelected: Boolean) {
        items.filter { it.item.id == item.id }.forEach {
            it.isSelected = isSelected
        }
        selectedItems.removeAll { it.id == item.id }
        if (isSelected) {
            selectedItems.add(item)
        }
        selectedItemsUpdated.value = true
    }

    fun setSelectedItems(newItems: Array<T>) {
        val ids = newItems.map { it.id }
        items.forEach {
            it.isSelected = it.item.id in ids
        }
        selectedItems.clear()
        selectedItems.addAll(newItems)
        selectedItemsUpdated.value = true
    }

    val T.id get() = itemToId(this)
}

fun <T> View.setupRemoteMultiSelectionDropDownList(
    name: String,
    viewModel: MultiSelectionDropDownListViewModel<T>,
    itemToString: (T) -> String,
    onSelectedItemsUpdated: (RemoteMultiSelectionDropDownList<T>.(List<T>) -> Unit)? = null,
    rootStyle: RuleSet? = null,
    defaultItems: List<T>? = null,
    showAutoComplete: Boolean = false,
    viewWidth: Dimension = 300.px,
    viewWidthFactory: (RuleSet.() -> Dimension)? = null,
    slug: String? = null,
    isDisabled: Boolean = false
): RemoteMultiSelectionDropDownList<T> {

    return RemoteMultiSelectionDropDownList(
        name,
        viewModel,
        defaultItems,
        rootStyle,
        itemToString,
        viewModel.itemToId,
        onSelectedItemsUpdated,
        showAutoComplete,
        viewWidthFactory ?: { viewWidth },
        slug,
        isDisabled
    ).apply {
        this@setupRemoteMultiSelectionDropDownList.mount(this)
    }
}

