package com.narbase.narcore.common.auth.basic

import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class PasswordEncoder {

    fun encode(password: String): String {
        val salt = generateSalt()
        val iterations = DEFAULT_ITERATIONS
        return getEncryptedPassword(password, salt, iterations)
    }

    private fun getEncryptedPassword(
        password: String,
        salt: String,
        iterations: Int
    ): String {
        val spec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), iterations, 256)

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val secret = factory.generateSecret(spec)
        val rawHash = secret.encoded
        val hashBase64 = Base64.getEncoder().encode(rawHash)
        val hash = String(hashBase64)
        return String.format("pbkdf2_sha256$%d$%s$%s", iterations, salt, hash)
    }

    private fun generateSalt(): String {
        var salt = ""
        val random = Random()
        (1..SALT_LENGTH).forEach {
            salt = "$salt${SALT_CHAR_SET[random.nextInt(SALT_CHAR_SET.length)]}"
        }
        return salt
    }

    fun checkPassword(password: String, hashedPassword: String): Boolean {
        val parts = hashedPassword.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size != 4) {
            println("wrong hash format")
            return false
        }
        val iterations = Integer.parseInt(parts[1])
        val salt = parts[2]
        val hash = getEncryptedPassword(password, salt, iterations)
        return hash == hashedPassword
    }

    companion object {
        const val SALT_LENGTH = 12
        const val SALT_CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"
        const val DEFAULT_ITERATIONS = 30_000
    }
}