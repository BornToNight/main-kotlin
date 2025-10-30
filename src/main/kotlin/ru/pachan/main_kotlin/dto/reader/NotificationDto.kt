package ru.pachan.main_kotlin.dto.reader

data class NotificationDto(
    val notification_id: Long,
    val person_id: Long,
    val count: Long,
)
