package com.narbase.narcore.web.utils.notifications

import com.narbase.narcore.web.common.AppConfig
import com.narbase.narcore.web.events.ServerConnectionEvent
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.storage.StorageManager
import com.narbase.narcore.web.utils.DataResponse
import com.narbase.narcore.web.utils.eventbus.EventBus
import com.narbase.narcore.web.utils.views.SnackBar
import kotlinx.browser.window
import org.w3c.dom.WebSocket
@JsExport
object NotificationsController {
    var isConnectedToServer = false

    enum class MessageTypes {
        Greeting,

    }

    open class Message(val type: String)

    /**
     * Call initializedWebSocket before using
     */
    private var webSocket: WebSocket? = null

    private fun initializedWebSocket(): WebSocket {
        val location = window.location
        val host = location.host
        val wsProtocol = if (location.protocol == "https:") "wss:" else "ws:"
        val baseUrl =
            if (AppConfig.isDev)
                ServerCaller.BASE_URL.replace("http://", "ws://").replace("https://", "ws://")
            else
                "$wsProtocol//$host"
        return WebSocket("${baseUrl}/public/api/user/v1/socket",
            StorageManager.accessToken?.let { arrayOf("ws_custom_auth", it) })
    }

    fun connect() {
        if (isConnectedToServer) {
            disconnect()
        }
        webSocket = initializedWebSocket()
        webSocket?.onclose = {
            isConnectedToServer = false
            EventBus.publish(ServerConnectionEvent(isConnected = false))
            val connectionRetryDuration = 5_000
            window.setTimeout({
                connect()
            }, connectionRetryDuration)
        }
        isConnectedToServer = true
        EventBus.publish(ServerConnectionEvent(isConnected = true))

        webSocket?.onmessage = { messageEvent ->
            messageEvent.data?.toString()?.let { data ->
                val message = JSON.parse<DataResponse<Message>>(data).data
                when (message.type) {
                    MessageTypes.Greeting.toString() -> {
                        SnackBar.showText("Greetings")
                    }
                }

            }
        }
    }


    fun disconnect() {
        webSocket?.onclose = {
            isConnectedToServer = false
            EventBus.publish(ServerConnectionEvent(isConnected = false))
        }
        webSocket?.close()
    }
}
