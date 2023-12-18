package kz.kamilaomar.moodtrackerback.models

data class RegistrationRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val password: String
)
