package kz.kamilaomar.moodtrackerback.service

import kz.kamilaomar.moodtrackerback.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.*
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")

        val authorities: List<GrantedAuthority> = user.roles.map { SimpleGrantedAuthority(it.name) }

        return User(
            user.username,
            user.password,
            authorities
        )
    }
}
