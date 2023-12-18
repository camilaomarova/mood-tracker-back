package kz.kamilaomar.moodtrackerback.controller

import kz.kamilaomar.moodtrackerback.models.RegisteredUserResponse
import kz.kamilaomar.moodtrackerback.models.RegistrationRequest
import kz.kamilaomar.moodtrackerback.service.TokenProvider
import kz.kamilaomar.moodtrackerback.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController (
    private val tokenProvider: TokenProvider,
    private val userService: UserService
) {

    @PostMapping("/api/users/register")
    fun registerUser(@RequestBody registrationRequest: RegistrationRequest): ResponseEntity<Any> {
        // TODO: add validation logic here for the registration request

        // Check if the email is already in use
        if (userService.isEmailTaken(registrationRequest.email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Email is already in use. Please login.")
        }

        // If the email is not in use, proceed with user registration
        val registeredUser = userService.registerUser(
            firstName = registrationRequest.firstName,
            lastName = registrationRequest.lastName,
            email = registrationRequest.email,
            username = registrationRequest.username,
            password = registrationRequest.password
        )

        // Return token for a registered user
        val token = tokenProvider.createToken(registeredUser.username!!)

        val response = RegisteredUserResponse(
            userId = registeredUser.id.toString(),
            firstName = registeredUser.firstName,
            lastName = registeredUser.lastName,
            email = registeredUser.email,
            username = registeredUser.username,
            password = registeredUser.password,
            token = token,
            roles = emptySet()
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
