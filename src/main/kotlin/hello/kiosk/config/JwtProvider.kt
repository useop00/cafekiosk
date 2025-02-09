package hello.kiosk.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider {

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    private val EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    fun generateToken(username: String): String {
        return createToken(username)
    }

    private fun createToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun isValid(token: String): Boolean {
        return try {
            parseClaims(token) != null
        } catch (e: Exception) {
            false
        }
    }

    fun getUsername(token: String): String {
        return parseClaims(token)?.subject ?: throw IllegalArgumentException("유효하지 않은 토큰입니다.")
    }


    private fun parseClaims(token: String): Claims? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            null
        }
    }
}