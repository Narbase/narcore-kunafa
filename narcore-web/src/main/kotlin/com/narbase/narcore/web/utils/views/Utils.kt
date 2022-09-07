package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.css.RuleSet
import com.narbase.kunafa.core.css.media

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2019/05/22.
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
