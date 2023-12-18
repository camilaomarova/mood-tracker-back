package kz.kamilaomar.moodtrackerback.config

import kz.kamilaomar.moodtrackerback.service.JwtTokenFilter
import kz.kamilaomar.moodtrackerback.service.TokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
    private val tokenProvider: TokenProvider
) : WebSecurityConfigurerAdapter() {

    @Value("\${jwt.secret}")
    private val jwtSecret: String = ""

    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long = 86400000 // 24 hours in milliseconds

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
    }

    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/public/**").permitAll()
            .antMatchers("/login").permitAll()
            .antMatchers("/api/users/register").permitAll()
            .anyRequest().authenticated() // Require authentication for all other requests
            .and()
            .addFilterBefore(JwtTokenFilter(tokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling().authenticationEntryPoint { request, response, authException ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
            }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
