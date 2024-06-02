package com.narbase.narcore.web.utils.metabase

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.percent
import com.narbase.narcore.web.utils.metabase.jwsSign
import com.narbase.narcore.web.utils.json
import com.narbase.narcore.web.utils.views.iframe


class MetabaseDashboardComponent(val dashBoardId: Int) : Component() {


    override fun View?.getView() = verticalLayout {

        style {
            width = 100.percent
            height = 100.percent
        }

        iframe {
            style {
                width = 100.percent
                height = 100.percent
            }
            element.src = getUrl()
        }
    }

    private fun getUrl(): String {
        val metabaseSiteURL = "https://testing.suhub.jobs/metabase"
        val secretKey = "5dafc9036fb8d3ff78edd9e01ff49639906517d2f02fadfabc824c2bb3d35875"

        val payload = json {
            "resource" to json {
                "dashboard" to dashBoardId
            }
            "params" to json { }
        }
        val token = jwsSign(payload, secretKey, json { })
        return "$metabaseSiteURL/embed/dashboard/$token#bordered=false&titled=false"
    }
}
