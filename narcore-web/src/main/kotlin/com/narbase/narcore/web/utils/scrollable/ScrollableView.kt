package com.narbase.narcore.web.utils.scrollable

import com.narbase.kunafa.core.components.Component
import com.narbase.kunafa.core.components.View

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/09/25.
 */

abstract class ScrollableView : Component() {

    open var childView: View? = null

    abstract fun refreshScrollHandler()

}
