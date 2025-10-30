package ru.pachan.main_kotlin.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Value("\${openapi.local-url}")
    private lateinit var localUrl: String

    @Value("\${openapi.dev-url}")
    private lateinit var devUrl: String

    @Bean
    fun myOpenApi(): OpenAPI {
        val localServer = Server().url(localUrl).description("Адрес локального сервера")
        val devServer = Server().url(devUrl).description("Адрес dev сервера")

        val info = Info().title("API документация Пет проекта")

        return OpenAPI().addSecurityItem(SecurityRequirement().addList("bearerAuth")).components(
            Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
        ).info(info).servers(listOf(localServer, devServer))
    }

}