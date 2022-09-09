package com.narbase.narcore.notification.email

import kotlinx.html.DIV

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun getBasicTemplate(title: String, body: DIV.() -> Unit): String {
    TODO("Use Kunafa ssr")
//
//    val classes = listOf(
//            stringRuleSet("*") {
//                boxSizing = "border-box"
//                margin = 0.px
//                padding = 0.px
//                fontFamily = "'Cairo', sans-serif"
//            })
//
//    val content = StringBuilder().appendHTML(false).html {
//        head {
//            meta {
//                charset = "utf-8"
//            }
//            style {
//                classes.forEach {
//                    unsafe { raw(it.toString()) }
//                }
//            }
//        }
//        body {
//            simpleStyle {
//                margin = st("32px")
//            }
//
//            div {
//
//                img {
//                    simpleStyle {
//                        maxWidth = 400.px
//                        height = 70.px
//                    }
//                    src = "https://narcore.narbase.com/public/img/logo.png"
//                }
//
//                h1 {
//                    simpleStyle {
//                        width = 100.percent
//                        marginTop = 32.px
//                        marginBottom = 16.px
//                        color = Color("#444")
//                        fontSize = 20.px
//                    }
//                    +title
//
//                }
//                clearBoth()
//                body()
//                clearBoth()
//                div {
//                    simpleStyle {
//                        width = 100.percent
//                        height = 1.px
//                        marginTop = 32.px
//                        marginBottom = 32.px
//                        backgroundColor = Color(0, 0, 0, 0.1)
//                        opacity = 0.2
//                    }
//                }
//                clearBoth()
//                span {
//                    simpleStyle {
//                        width = 100.percent
//                        marginTop = 16.px
//                        marginBottom = 16.px
//                        fontSize = 14.px
//                        color = Color("#666")
//                    }
//                    +"© ${Calendar.getInstance().get(Calendar.YEAR)} Narbase Technologies. All rights reserved."
//                }
//            }
//        }
//    }
//    return content.toString()
}

