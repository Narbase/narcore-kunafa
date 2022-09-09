package com.narbase.narcore.notification.email

import com.narbase.narcore.deployment.EmailConfig
import com.narbase.narcore.notification.email.templates.newAccountTemplate
import kotlinx.html.DIV
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object EmailSender {

    var props: Properties = Properties()
    private val executor: ThreadPoolExecutor = Executors.newCachedThreadPool() as ThreadPoolExecutor
    private val logger = LoggerFactory.getLogger(this::class.java.simpleName)

    init {
        props.put("mail.smtp.host", "smtp.zoho.com")
        props.put("mail.smtp.socketFactory.port", "587")
        props.put(
            "mail.smtp.socketFactory.class",
            "javax.net.ssl.SSLSocketFactory"
        )
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.port", "587")
        props.put("mail.smtp.starttls.enable", "true")

    }

    fun notifyNewAccountCreated(recipientName: String, url: String, recipientEmail: String) {
        val content = newAccountTemplate(recipientName, url)
        sendEmailWithContent(recipientEmail, "Welcome", content)
    }

    /* todo// add Kunafa server side
        fun sendEmail(recipientEmail: String, title: String, body: String): Int {
            return sendEmail(recipientEmail, title, body = {
                div {
                    simpleStyle {
                        width = 100.percent
                        marginBottom = 32.px
                        color = Color("#444")
                        fontSize = 16.px
                    }

                    p {
                        +body
                    }
                }
            })
        }
    */

    fun sendEmail(recipientEmail: String, title: String, body: DIV.() -> Unit): Int {
        val content = getBasicTemplate(title, body)
        return sendEmailWithContent(recipientEmail, title, content)
    }

    fun sendEmailWithContent(recipientEmail: String, title: String, content: String): Int {
        if (executor.poolSize > 100) {
            logger.error("SMS Thread pool size is 20. Will not accept further requests.")
            return -1
        }
        logger.info("SMS Thread pool size is ${executor.poolSize}.")
        executor.submit {
            sendEmailActual(recipientEmail, title, content)
        }
        return 0
    }

    fun sendEmailActual(recipientEmail: String, title: String, content: String) {
        try {
            logger.info("Sending email to $recipientEmail")
            val message = getMessageContent(recipientEmail, title)
            message.setContent(content, "text/html; charset=UTF-8")
            Transport.send(message)
            logger.info("Email sent successfully to $recipientEmail")
        } catch (e: Throwable) {
            logger.info("Failed to send email to $recipientEmail")
            e.printStackTrace()
        }
    }

    private fun getMessageContent(recipientEmail: String, messageSubject: String): MimeMessage {
        val message = MimeMessage(Session.getDefaultInstance(props, mailAuthenticator()))
        message.setFrom(InternetAddress(EMAIL_SENDING, EMAIL_SENDER_PERSONAL_NAME))
        message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(recipientEmail)
        )
        message.subject = messageSubject
        return message
    }

    class mailAuthenticator : Authenticator() {
        override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
            return javax.mail.PasswordAuthentication(EMAIL_SENDING, EMAIL_PASSWORD)
        }
    }

    val EMAIL_SENDING = EmailConfig.email
    val EMAIL_SENDER_PERSONAL_NAME: String = TODO("Missing EMAIL_SENDER_PERSONAL_NAME")
    val EMAIL_PASSWORD = EmailConfig.password

}