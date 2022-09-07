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
import com.narbase.narcore.web.events.EscapeClickedEvent
import com.narbase.narcore.web.network.makeNotVisible
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.storage.bidirectional
import com.narbase.narcore.web.utils.BasicUiState
import com.narbase.narcore.web.utils.PopupZIndex
import com.narbase.narcore.web.utils.eventbus.LifecycleSubscriber
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import org.w3c.dom.events.EventListener


class DropDownWithCustomList<T>(
    private val name: String,
    private val viewModel: DropDownWithCustomListViewModel<T>,
    private val selectedItems: List<T> = listOf(),
    private val rootStyle: RuleSet? = null,
    private var itemToString: (T) -> String,
    private val slug: String? = null,
    private val onDeleteITem: () -> Unit,
    private val block: LinearLayout.() -> Unit
) : Component() {

    private var dropDownTextView: TextView? = null
    private var dropDownHeader: LinearLayout? = null
    private var dropDownListView: View? = null
    private var bottomReferenceView: LinearLayout? = null
    private var dropDownListLayout: LinearLayout? = null

    private val selectedItemsViews = mutableMapOf<String, LinearLayout>()

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
        setupObservers()
        viewModel.onViewCreated()
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
                hiddenBackground.element.style.zIndex = PopupZIndex.getTopIndex().toString()
                dropDownListAndSearchRootLayout.element.style.zIndex = PopupZIndex.getTopIndex().toString()
                makeVisible(hiddenBackground, dropDownListAndSearchRootLayout)
                movePopupToListLayout()
                window.addEventListener("resize", eventListener)
            } else {
                PopupZIndex.restoreTopIndex()
                makeNotVisible(hiddenBackground, dropDownListAndSearchRootLayout)
                window.removeEventListener("resize", eventListener)
                searchText?.text = ""
            }
        }
        viewModel.isClear.observe { isClear ->
            isClear ?: return@observe
            if (isClear) {
                dropDownHeader?.clearAllChildren()
                dropDownHeader?.apply { dropDownTextView = listTitle() }
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
                        viewModel.setClear()
                        onDeleteITem()
                        e.stopPropagation()
                    }
                }
            }
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


    fun addItem(newItem: T) {
        dropDownTextView?.text = itemToString(newItem)
        viewModel.setSelected()
        viewModel.hideList()
    }

    override fun View?.getView() = verticalLayout {
        id = "${slug ?: "insurance"}-custom-dropDownList-rootView"
        style {
            width = matchParent
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

            onClick = { viewModel.showList() }

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


    private fun updateItems() {
        val layout = dropDownListLayout ?: return
        layout.clearAllChildren()
        layout.apply {
            block()
        }
        dropDownListAndSearchRootLayout.element.onscroll = {
            dropDownListAndSearchRootLayout.element.apply {
                if (scrollTop + offsetHeight >= scrollHeight) {
                    viewModel.getNextItems()
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
        viewModel.setClear()
    }
}

class DropDownWithCustomListViewModel<T>(
    private val isMock: Boolean,
    private val mockList: Array<SelectableItem<T>>,
    private val getRemoteItems: (page: Int) -> Unit
) {
    val isListShown: Observable<Boolean> = Observable()
    val isClear = Observable<Boolean>()
    var items: Array<SelectableItem<T>> = arrayOf()

    var page: Int = 0
    var size: Int = 30

    var loadingListState = Observable<BasicUiState>()
    var hasReachedEnd = false

    var searchTerm: String? = null

    fun onViewCreated() {
        getInitialItems()
        hideList()
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
            getRemoteItems(page)
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
        isClear.value = true
    }

    fun setSelected() {
        isClear.value = false
    }

    fun setItems(newItems: Array<T>) {
        items = newItems.map {
            SelectableItem(it, false)
        }.toTypedArray()
        if (newItems.size < size)
            hasReachedEnd = true
        loadingListState.value = BasicUiState.Loaded
    }
}

fun <T> View.setupDropDownWithCustomList(
    name: String,
    viewModel: DropDownWithCustomListViewModel<T>,
    selectedItems: List<T> = listOf(),
    rootStyle: RuleSet? = null,
    itemToString: (T) -> String,
    slug: String? = null,
    onDeleteITem: () -> Unit,
    block: LinearLayout.() -> Unit
): DropDownWithCustomList<T> {

    return DropDownWithCustomList(
        name,
        viewModel,
        selectedItems,
        rootStyle,
        itemToString,
        slug,
        onDeleteITem,
        block
    ).apply {
        this@setupDropDownWithCustomList.mount(this)
    }
}

