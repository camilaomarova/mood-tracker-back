package kz.kamilaomar.moodtrackerback.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenProvider(
    @Value("\${jwt.secret}")
    private val secretKey: String,
    @Value("\${jwt.expiration}")
    private val expirationMillis: Long,
    private val userDetailsService: UserDetailsService
) {

    fun createToken(username: String): String {
        val now = Date()
        val validity = Date(now.time + expirationMillis)

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(Keys.hmacShaKeyFor(stringToByteArray(secretKey)), SignatureAlgorithm.HS256)
            .compact()
//        return UUID.randomUUID().toString()
    }

    fun getUsernameFromToken(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(stringToByteArray(secretKey)))
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(stringToByteArray(secretKey)))
                .build()
                .parseClaimsJws(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val username = getUsernameFromToken(token)
        // You may need to load user details from your user service here
        val userDetails =  userDetailsService.loadUserByUsername(username) // Load UserDetails based on the username
            return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    private fun stringToByteArray(input: String): ByteArray {
        val result = ByteArray(input.length * 2) // Assuming a single character takes 2 bytes in UTF-16

        for (i in 0 until input.length) {
            val char = input[i]
            result[i * 2] = (char.toInt() and 0xFF).toByte()
            result[i * 2 + 1] = (char.toInt() ushr 8 and 0xFF).toByte()
        }

        return result
    }
}
