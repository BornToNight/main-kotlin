package ru.pachan.main_kotlin.dto.dictionary

import java.io.Serializable

data class PaginatedResponse<T>(
    val total: Long,
    val result: List<T>,
) : Serializable
