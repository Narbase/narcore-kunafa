package com.narbase.narcore.main.sms

import com.narbase.narcore.data.enums.SmsMessageStatus
import com.narbase.narcore.data.tables.SmsRecordTable
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.net.URL
import java.net.URLEncoder

object SmsSender {
    private val logger = LoggerFactory.getLogger("SmsSender")
    private val senderClient = HttpClient(Apache)


    fun sendMessage(data: SmsMessageData) {

        val phones = data.phones.toList()
        val message = data.message

        addSmsMessageRecord(
            phones,
            message,
            SmsMessageStatus.Pending
        )

        GlobalScope.launch {
            val messageStatus = sendMessageToProvider(phones, message)
            addSmsMessageRecord(
                phones,
                message,
                messageStatus,
            )
        }
    }

    fun sendMessageToProvider(phones: List<Long>, message: String): SmsMessageStatus {
        logger.info("Sending $message to ${phones.joinToString()}")
        val encodedMessage = URLEncoder.encode(message)
        val url = BASE_URL
            .replace("MessageBody", encodedMessage)
            .replace("{ReceiversNumbers}", phones.joinToString(separator = ";"))
        return sendMessageUsingUrl(url)
    }

    private fun sendMessageUsingUrl(url: String, retriesCounter: Int = 0): SmsMessageStatus {
        logger.info("Sending sms. retriesCounter is $retriesCounter")
        return runBlocking {
            try {
                val response = senderClient.request {
                    url(URL(url))
                    method = HttpMethod.Get
                }
                val status = response.status.getMessageStatus()
                logger.info("Sent with status $status")
                status

            } catch (e: Exception) {
                e.printStackTrace()
                if (retriesCounter < 2) {
                    logger.info("Trying again")
                    sendMessageUsingUrl(url, retriesCounter + 1)
                } else {
                    logger.info("Failed to send SMS")
                    println("Failed to send SMS")
                    SmsMessageStatus.Failed
                }
            }
        }
    }

    private fun HttpStatusCode.getMessageStatus() = if (isSuccess()) SmsMessageStatus.Sent else {
        println(this)
        SmsMessageStatus.Failed
    }

    private fun addSmsMessageRecord(
        phones: List<Long>,
        message: String,
        status: SmsMessageStatus,
    ) {
        transaction {
            SmsRecordTable.insert {
                it[SmsRecordTable.message] = message
                it[SmsRecordTable.phones] = phones.map { it.toString() }
                it[SmsRecordTable.status] = status
            }
        }
    }


    //    val SMS_USER: String = TODO()
//    val SMS_PASSWORD: String = TODO()
//    val SMS_SENDER: String = TODO()
//    val BASE_URL =
//        "https://www.airtel.sd/bulksms/webacc.aspx?user=$SMS_USER&pwd=$SMS_PASSWORD&smstext=MessageBody&Sender=$SMS_SENDER&Nums={ReceiversNumbers}"
    val BASE_URL: String = TODO()

}