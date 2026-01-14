package ru.pachan.main_kotlin.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.pachan.main_kotlin.util.enums.AuthorityEnum

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class WebSecurityConfig(

    @param:Value("\${spring.boot.admin.client.username}")
    private val adminUsername: String,

    @param:Value("\${spring.boot.admin.client.password}")
    private val adminPassword: String,

    private val requestProvider: RequestProvider,

    ) {

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    // TODO выглядит лишним
                    .requestMatchers("api/auth/**").hasAuthority(AuthorityEnum.VERIFIED_TOKEN.authority)
                    .requestMatchers("actuator/**").hasAuthority(AuthorityEnum.ACTUATOR_ADMIN.authority)
                    .requestMatchers("instances/**").hasAuthority(AuthorityEnum.ACTUATOR_ADMIN.authority)
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtFilter(requestProvider, adminUsername, adminPassword),
                BasicAuthenticationFilter::class.java
            )
            .cors { it.configurationSource(corsConfigurationSource()) }

        return httpSecurity.build()
    }

    // TODO исправить в будущем по нормальному
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring()
                .requestMatchers("api/auth/generate")
                .requestMatchers("swagger")
                .requestMatchers("swagger-ui/**")
                .requestMatchers("apiDocs/**")
                .requestMatchers("graphiql/**")
                .requestMatchers("graphiql")
        }
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:3000", "http://localhost:5004")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
