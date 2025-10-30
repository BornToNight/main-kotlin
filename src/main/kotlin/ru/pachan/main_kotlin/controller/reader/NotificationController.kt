package ru.pachan.main_kotlin.controller.reader

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pachan.main_kotlin.dto.reader.NotificationDto
import ru.pachan.main_kotlin.service.reader.NotificationService

@RestController
@RequestMapping("api/main/notification")
@Tag(name = "Notification")
class NotificationController(
    private val service: NotificationService,
) {

    @Operation(summary = "Возвращение по переданному id")
    @GetMapping("/{id}")
    fun getOne(
        @PathVariable id: Long,
    ): ResponseEntity<NotificationDto> {
        return ResponseEntity.ok(service.findByIdNotification(id))
    }

    @Operation(summary = "Возвращение по переданному id")
    @GetMapping("/personId/{id}")
    fun getOneByPersonId(
        @PathVariable id: Long,
    ): ResponseEntity<NotificationDto> {
        return ResponseEntity.ok(service.findByPersonIdNotification(id))
    }

}