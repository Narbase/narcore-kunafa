package com.narbase.narcore.web.utils.scrollable

import com.narbase.kunafa.core.components.Component
import com.narbase.kunafa.core.components.View

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

abstract class ScrollableView : Component() {

    open var childView: View? = null

    abstract fun refreshScrollHandler()

}
