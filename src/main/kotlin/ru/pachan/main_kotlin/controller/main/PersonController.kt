package ru.pachan.main_kotlin.controller.main

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.pachan.main_kotlin.dto.dictionary.PaginatedResponse
import ru.pachan.main_kotlin.dto.main.PersonDto
import ru.pachan.main_kotlin.dto.main.PersonNameAndOrgNameDto
import ru.pachan.main_kotlin.dto.main.PersonNameDto
import ru.pachan.main_kotlin.model.main.Person
import ru.pachan.main_kotlin.service.main.PersonService

@RestController
@RequestMapping("api/main/person")
@Tag(name = "Person")
class PersonController(
    private val service: PersonService,
) {

    private val log = LoggerFactory.getLogger(PersonController::class.java)

    @Operation(summary = "Возвращение всех с фильтрацией")
    @GetMapping
    fun getAll(
        @ParameterObject pageable: Pageable,
        @Parameter(description = "Фильтр по имени сотрудника")
        @RequestParam(required = false) firstName: String?,
        @Parameter(description = "Фильтр по именам сотрудника")
        @RequestParam(required = false) firstNames: List<String>?,
    ): ResponseEntity<PaginatedResponse<PersonDto>> {
        // EXPLAIN_V Пример для сохранения в elastic
        log.info("PersonController getAll")
        return ResponseEntity.ok(service.getAll(pageable, firstName, firstNames))
    }

    @Operation(summary = "Возвращение всех имён persons")
    @GetMapping("/names")
    fun getAllNames(
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<PaginatedResponse<PersonNameDto>> {
        return ResponseEntity.ok(service.getAllNames(pageable))
    }

    @Operation(summary = "Возвращение всех имён persons с именем организации")
    @GetMapping("/namesAndOrgNames")
    fun getAllNamesAndOrgNames(
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<PaginatedResponse<PersonNameAndOrgNameDto>> {
        return ResponseEntity.ok(service.getAllNamesAndOrgNames(pageable))
    }

    @Operation(summary = "Возвращение по переданному id")
    @GetMapping("/{id}")
    fun getOne(
        @PathVariable id: Long,
    ): ResponseEntity<Person> {
        return ResponseEntity.ok(service.getOne(id))
    }

    @Operation(summary = "Создание")
    @PostMapping
    fun createOne(
        @Valid @RequestBody person: Person,
    ): ResponseEntity<Person> {
        return ResponseEntity.ok(service.createOne(person))
    }

    @Operation(summary = "Обновление")
    @PutMapping("/{id}")
    fun updateOne(
        @PathVariable id: Long,
        @Valid @RequestBody person: Person,
    ): ResponseEntity<Person> {
        return ResponseEntity.ok(service.updateOne(id, person))
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

}