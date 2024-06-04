package com.narbase.narcore.dto.domain.user.websocket

import com.narbase.narcore.dto.common.enums.MessageTypes
import kotlin.js.JsExport

@JsExport
object WebSocketDtos {

    open class Message(val type: String)

    class Greeting(val greeting: String) : Message(MessageTypes.Greeting.toString())
}