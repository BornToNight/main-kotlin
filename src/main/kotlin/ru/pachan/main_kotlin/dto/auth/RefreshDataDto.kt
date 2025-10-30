package ru.pachan.main_kotlin.dto.auth

data class RefreshDataDto(
    val refresh: String,
    val token: String,
    val roleId: Long,
    val userId: Long,
)
