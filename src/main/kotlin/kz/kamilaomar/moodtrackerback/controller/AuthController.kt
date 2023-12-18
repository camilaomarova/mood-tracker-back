package kz.kamilaomar.moodtrackerback.controller

import kz.kamilaomar.moodtrackerback.models.LoginRequest
import kz.kamilaomar.moodtrackerback.models.LoginResponse
import kz.kamilaomar.moodtrackerback.service.TokenProvider
import kz.kamilaomar.moodtrackerback.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val userService: UserService
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val user = userService.findByUsername(loginRequest.username)

        return if (user != null && passwordEncoder.matches(loginRequest.password, user.password)) {
            val token = tokenProvider.createToken(user.username!!)

            val loginResponse = LoginResponse(
                token = token,
                userId = user.id.toString()
            )

            ResponseEntity.ok(loginResponse)
        } else {
            ResponseEntity.status(401).body(LoginResponse(token = "Invalid credentials", userId = null))
        }
    }
}
