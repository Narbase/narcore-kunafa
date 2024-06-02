package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.css.RuleSet
import com.narbase.kunafa.core.css.media

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun RuleSet.smallScreen(rules: RuleSet.() -> Unit) {
    media(MEDIA_WIDTH_SMALL, rules)
}

fun RuleSet.bigScreen(rules: RuleSet.() -> Unit) {
    media(MEDIA_WIDTH_BIG, rules)
}

fun RuleSet.mediumScreen(rules: RuleSet.() -> Unit) {
    media(MEDIA_WIDTH_MEDIUM, rules)
}

fun RuleSet.biggerThanMediumScreen(rules: RuleSet.() -> Unit) {
    media(MEDIA_BIGGER_THAN_WIDTH_MEDIUM, rules)
}
