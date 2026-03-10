package ru.pachan.main_kotlin.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.pachan.main_kotlin.util.enums.AuthorityEnum

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class WebSecurityConfig(
    private val requestProvider: RequestProvider,
) {

    @Bean
    @Order(1)
    fun managementFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .securityMatcher("actuator/**")
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    // EXPLAIN_V Без Spring Admin достаточно было бы 2 ЭП этих
//                    .requestMatchers("actuator/prometheus").permitAll()
//                    .requestMatchers("actuator/health").permitAll()
                    .requestMatchers("actuator/**").permitAll()
                    .anyRequest().denyAll()
            }
        return httpSecurity.build()
    }

    @Bean
    @Order(2)
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    // TODO выглядит лишним
                    .requestMatchers("api/auth/**").hasAuthority(AuthorityEnum.VERIFIED_TOKEN.authority)
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtFilter(requestProvider),
                UsernamePasswordAuthenticationFilter::class.java
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
