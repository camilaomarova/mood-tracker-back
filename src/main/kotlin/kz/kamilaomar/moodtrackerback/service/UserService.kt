package kz.kamilaomar.moodtrackerback.service

import kz.kamilaomar.moodtrackerback.models.Role
import kz.kamilaomar.moodtrackerback.models.User
import kz.kamilaomar.moodtrackerback.repository.UserRepository
import org.springframework.security.crypto.password.*
import org.springframework.stereotype.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun registerUser(firstName: String, lastName: String,
                     email:String, username: String, password: String): User {
        val hashedPassword = passwordEncoder.encode(password)
        val user = User(firstName = firstName, lastName = lastName,
            email = email, username = username,
            password = hashedPassword, roles = setOf(Role(1, "ROLE_USER")))
        return userRepository.save(user)
    }

    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun isEmailTaken(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }
}
