package ru.pachan.main_kotlin.controller.main.certificate

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pachan.main_kotlin.dto.dictionary.PaginatedResponse
import ru.pachan.main_kotlin.model.main.Certificate
import ru.pachan.main_kotlin.service.main.CertificateService

@RestController
@RequestMapping("api/main/certificate")
@Tag(name = "Certificate")
class CertificateController(
    private val service: CertificateService,
) {

    @Operation(summary = "Возвращение всех с фильтрацией")
    @GetMapping
    fun getAll(
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<PaginatedResponse<Certificate>> {
        return ResponseEntity.ok(service.getAll(pageable))
    }

    @Operation(summary = "Возвращение по переданному id")
    @GetMapping("/{id}")
    fun getOne(
        @PathVariable id: Long,
    ): ResponseEntity<Certificate> {
        return ResponseEntity.ok(service.getOne(id))
    }

    @Operation(summary = "Создание")
    @PostMapping
    fun createOne(
        @Valid @RequestBody certificate: Certificate,
    ): ResponseEntity<Certificate> {
        return ResponseEntity.ok(service.createOne(certificate))
    }

    @Operation(summary = "Обновление")
    @PutMapping("/{id}")
    fun updateOne(
        @PathVariable id: Long,
        @Valid @RequestBody certificate: Certificate,
    ): ResponseEntity<Certificate> {
        return ResponseEntity.ok(service.updateOne(id, certificate))
    }

    @ApiResponse(responseCode = "204")
    @Operation(summary = "Удаление по переданному id")
    @DeleteMapping("/{id}")
    fun deleteOne(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        try {
            service.deleteOne(id)
            return ResponseEntity.status(NO_CONTENT).build()
        } catch (e: Exception) {
            return ResponseEntity.status(NO_CONTENT).build()
        }
    }

    @Operation(summary = "Создание по 500 пользователей в 3-ёх потоках")
    @PostMapping("/massiveCreation")
    fun massiveCreation() {
        service.massiveCreation()
    }

}