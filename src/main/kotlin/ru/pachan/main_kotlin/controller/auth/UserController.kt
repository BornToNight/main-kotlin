package ru.pachan.main_kotlin.controller.auth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pachan.main_kotlin.dto.dictionary.PaginatedResponse
import ru.pachan.main_kotlin.model.auth.User
import ru.pachan.main_kotlin.service.auth.UserService

@CrossOrigin
@RestController
@RequestMapping("api/auth/user")
@Tag(name = "User")
class UserController(
    private val service: UserService,
) {

    @Operation(summary = "Создание пользователя")
    @PostMapping
    fun createOne(
        @RequestBody user: User,
    ): ResponseEntity<User> {
        return ResponseEntity.ok(service.createOne(user))
    }

    @Operation(summary = "Возвращение всех пользователей с фильтрацией")
    @GetMapping
    fun getAll(
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<PaginatedResponse<User>> {
        return ResponseEntity.ok(service.getAll(pageable))
    }

    @Operation(summary = "Возвращение пользователя по переданному id")
    @GetMapping("/{id}")
    fun getOne(

        @Parameter(description = "Id пользователя")
        @PathVariable id: Long,

        @RequestHeader("Authorization") authToken: String,

        ): ResponseEntity<User> {
        return ResponseEntity.ok(service.getOne(id, authToken))
    }

    @Operation(summary = "Обновление пользователя", description = "Обновляет данные пользователя с переданным id")
    @PostMapping("/{id}")
    fun updateOne(

        @Parameter(description = "Id пользователя")
        @PathVariable id: Long,

        @RequestBody user: User,

        @RequestHeader("Authorization") authToken: String,

        ): ResponseEntity<User> {
        return ResponseEntity.ok(service.updateOne(id, authToken, user))
    }

    @ApiResponse(responseCode = "204", content = [Content(schema = Schema())])
    @Operation(summary = "Удаление пользователя по переданному id")
    @DeleteMapping("/{id}")
    fun deleteOne(@PathVariable id: Long): ResponseEntity<Void> {
        service.deleteOne(id)
        return ResponseEntity.noContent().build()
    }

}