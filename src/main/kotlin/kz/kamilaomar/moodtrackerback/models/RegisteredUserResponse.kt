package kz.kamilaomar.moodtrackerback.models

data class RegisteredUserResponse (
    val userId: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val username: String?,
    val password: String?, // TODO: use password hashing
    val roles: Set<Role>,
    val token: String?
)
