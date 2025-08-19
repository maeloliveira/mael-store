package com.bookstore.mael.store.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfiguration {
    private val PUBLIC_MATCHERS = arrayOf<String>(
        "/customer"
    )

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.POST, *PUBLIC_MATCHERS).permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin(withDefaults())
            .httpBasic(withDefaults())

        return http.build()
    }

    @Bean
    fun userDetailsService(passwordEncoder: BCryptPasswordEncoder): UserDetailsService {
        val user: UserDetails = User.builder()
            .username("user")
            .password("password")
            .roles("USER")
            .build();
        val admin = User.builder()
            .username("admin")
            .password("password")
            .roles("ADMIN", "USER")
            .build();
        return InMemoryUserDetailsManager(user, admin);
    }

}
