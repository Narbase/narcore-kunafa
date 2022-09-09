package com.narbase.narcore.notification.email.templates

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun newAccountTemplate(name: String, url: String): String {
    TODO("Use Kunafa ssr")
}
/*
fun newAccountTemplate(drName: String, url: String): String {
    val classes = listOf(
            stringRuleSet("*") {
                boxSizing = "border-box"
                margin = 0.px
                padding = 0.px
                fontFamily = "'Cairo', sans-serif"
                lineHeight = 1.6.em.toString()
            })

    val content = StringBuilder().appendHTML(false).html {
        head {
            meta {
                charset = "utf-8"
            }
            style {
                classes.forEach {
                    unsafe { raw(it.toString()) }
                }
            }
        }
        body {
            simpleStyle {
                margin = st("32px")
                backgroundColor = Color(250, 250, 250)
            }

            div {
                simpleStyle {
                    width = 100.percent
                }

                div {
                    simpleStyle {
                        width = 100.percent
                        paddingTop = 24.px
                    }
                    img {
                        simpleStyle {
                            maxWidth = 400.px
                            height = 100.px
                            minHeight = 100.px
                            marginBottom = 32.px
                            display = "block"
                            margin = st("0 auto 32px")

                        }
                        src = "https://narcore.narbase.com/public/img/logo.png"
                    }
                }
                br { }

                div {
                    simpleStyle {
                        width = 100.percent
                    }
                    cardView(drName, url)
                }

                clearBoth()
                span {
                    simpleStyle {
                        width = wrapContent
                        fontSize = 14.px
                        color = Color("#666")
                        display = "table"
                        margin = st("32px auto 16px")
                    }
                    +"© ${Calendar.getInstance().get(Calendar.YEAR)} Narbase Technologies. All rights reserved."
                }
            }
        }
    }
    println(content.toString())
    return content.toString()
}
*/


