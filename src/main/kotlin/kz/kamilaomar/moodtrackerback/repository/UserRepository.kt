package kz.kamilaomar.moodtrackerback.repository

import kz.kamilaomar.moodtrackerback.models.User
import org.springframework.data.jpa.repository.*
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?

    fun existsByEmail(email: String): Boolean
}
