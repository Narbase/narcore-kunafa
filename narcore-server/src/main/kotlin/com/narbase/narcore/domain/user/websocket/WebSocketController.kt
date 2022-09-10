package com.narbase.narcore.domain.user.websocket

import com.google.gson.Gson
import com.narbase.narcore.common.DataResponse
import com.narbase.narcore.common.auth.myJwtVerifier
import com.narbase.narcore.common.exceptions.UnauthenticatedException
import com.narbase.narcore.domain.user.myWsCustomAuthHeader
import com.narbase.narcore.dto.models.roles.Privilege
import io.ktor.server.request.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter


object WebSocketController {
    private val logger by lazy { setupLogger() }

    val clientSessions: MutableList<ClientSessionData> = mutableListOf()
    val gson = Gson()

    private val executor: ThreadPoolExecutor = Executors.newCachedThreadPool() as ThreadPoolExecutor
//    val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob() )

    private fun setupLogger(): Logger {
        val log = Logger.getLogger(this::class.java.simpleName)
        try {
            val file = File("log/web_socket_log.log")
            if (file.parentFile.exists().not()) {
                file.parentFile.mkdir()
            }
            val fh = FileHandler(file.absolutePath, 10_000_000, 5, true).apply {
                formatter = SimpleFormatter()
            }
            log.addHandler(fh)
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return log
    }

    /*
        fun startPeriodically() {
    //        return
            scheduleRepeated(1L, TimeUnit.MILLISECONDS.toMillis(1)) {
    //            println("Sessions ${clientSessions.size}")
                send(DataResponse(BillUpdatedMessage(
                        UpdatedBillDetailsDto("", UUID.randomUUID(), UUID.randomUUID(), "patientName", null, BillStatusDto.NotPaid, listOf()))))
            }

        }
    */

    enum class MessageTypes {
        Greeting,
    }

    open class Message(val type: String)
    class Greeting(val greeting: String) : Message(MessageTypes.Greeting.toString())

    class ClientSessionData(
        val session: DefaultWebSocketServerSession, val clientId: UUID,
        val privileges: List<Privilege>
    )

    suspend fun handle(session: DefaultWebSocketServerSession) {
        val token = session.call.request.header("Sec-WebSocket-Protocol")?.removePrefix("$myWsCustomAuthHeader, ")
            ?: throw UnauthenticatedException()
        val clientId =
            myJwtVerifier?.verify(token)?.claims?.get("clientId")?.let { UUID.fromString(it.asString()) }
        val privileges =
            myJwtVerifier?.verify(token)?.claims?.get("privileges")?.asArray(String::class.java)
                ?.mapNotNull { privilegeString ->
                    Privilege.values().firstOrNull { it.name == privilegeString }
                } ?: listOf()
        clientId?.let { clientSessions.add(ClientSessionData(session, clientId, privileges)) }
        logger.info("New client connected: $clientId")
        send(DataResponse(Greeting("Hello")))

        listenForIncoming(session, clientId)
    }

    private suspend fun listenForIncoming(session: DefaultWebSocketServerSession, clientId: UUID?) {
        try {
            for (frame in session.incoming) {
                handleReceivedFrames(frame, clientId)
            }
        } catch (e: ClosedReceiveChannelException) {
            logger.info("onClose: $clientId")
            println("onClose ${session.closeReason.await()}")
            clientSessions.filter { it.session == session }.forEach { it.session.close() }
            clientSessions.removeAll { it.session == session }
        } catch (e: Throwable) {
            logger.info("onError: $clientId")
            println("onError ${session.closeReason.await()}")
            e.printStackTrace()
            clientSessions.filter { it.session == session }.forEach { it.session.close() }
            clientSessions.removeAll { it.session == session }
        }
    }

    private fun handleReceivedFrames(frame: Frame, clientId: UUID?) {
        if (frame is Frame.Text) {
            val text = frame.readText()
        }
    }

    fun send(
        msg: DataResponse<Message>,
        clientId: UUID? = null,
        excludedId: Array<UUID?> = arrayOf(),
        privileges: List<Privilege> = Privilege.values().toList()
    ) {
        catchAndLog {
            logger.info("Sending ${msg.dto?.type}. Sessions: ${clientSessions.size}")
            if (executor.poolSize > 20) {
                logger.severe("Thread pool size is 20. Will not accept further requests.")
                return
            }
            logger.info("Thread pool size is ${executor.poolSize}.")
            executor.submit {
                runBlocking {
                    catchAndLog {
                        sendAsync(msg, clientId, excludedId, privileges)
                    }
                }
            }
        }
    }

    private inline fun catchAndLog(block: () -> Unit) {
        try {
            block()
        } catch (t: Throwable) {
            logger.severe(t.stackTraceToString())
            t.printStackTrace()
        }
    }

    private suspend fun sendAsync(
        msg: DataResponse<Message>,
        clientId: UUID?,
        excludedId: Array<UUID?>,
        privileges: List<Privilege>
    ) {
        logger.info("Sending ${msg.dto?.type}. Sessions: ${clientSessions.size}")
        clientSessions.filter { client ->
            (client.privileges.any { it in privileges } or (client.clientId == clientId)) && client.clientId !in excludedId
        }.forEach {
            try {
                val jsonMessage = gson.toJson(msg)
                logger.info("Sending to ${it.clientId}: $jsonMessage")
                it.session.send(Frame.Text(jsonMessage))
                logger.info("Sending ${msg.dto?.type} successful")
            } catch (e: Exception) { //Assume session has been closed
                logger.info("Session closed: ${it.clientId}")
                e.printStackTrace()
                it.session.close()
                clientSessions.remove(it)
            }
        }
    }
}