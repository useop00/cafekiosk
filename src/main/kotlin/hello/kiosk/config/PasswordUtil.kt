package hello.kiosk.config

import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.*
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

object PasswordUtil {
    private const val SALT_LENGTH = 16

    fun generateSalt(): String {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun verifyPassword(inputPassword: String, salt: String, storedPassword: String): Boolean {
        return storedPassword == hashPassword(inputPassword, salt)
    }

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val saltedPassword = password + salt
        md.update(saltedPassword.toByteArray(UTF_8))
        return Base64.getEncoder().encodeToString(md.digest())
    }
}