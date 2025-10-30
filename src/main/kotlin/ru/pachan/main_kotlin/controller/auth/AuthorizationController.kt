package ru.pachan.main_kotlin.controller.auth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pachan.main_kotlin.dto.auth.AuthorizationDto
import ru.pachan.main_kotlin.dto.auth.RefreshDataDto
import ru.pachan.main_kotlin.service.auth.AuthorizationService

@CrossOrigin
@RestController
@RequestMapping("api/auth")
@Tag(name = "Authorization")
class AuthorizationController(
    private val service: AuthorizationService,
) {

    @Operation(
        summary = "Генерация JWT",
        description = "Генерирует JWT, если переданы верный логин и пароль"
    )
    @PostMapping("/generate")
    fun generate(
        @RequestBody authorizationDto: AuthorizationDto,
    ): ResponseEntity<RefreshDataDto> {
        return ResponseEntity.ok(service.generate(authorizationDto))
    }

    @Operation(
        summary = "Обновление JWT",
        description = "Обновляет JWT, по переданному старому JWT"
    )
    @GetMapping("/refresh")
    fun refresh(
        @RequestHeader("Authorization") authToken: String,
    ): ResponseEntity<RefreshDataDto> {
        return ResponseEntity.ok(service.refresh(authToken))
    }

}