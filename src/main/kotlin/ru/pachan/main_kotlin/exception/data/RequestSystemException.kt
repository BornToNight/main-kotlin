package ru.pachan.main_kotlin.exception.data

import org.springframework.http.HttpStatus

// EXPLAIN_V Нужен, т.к. CircuitBreaker и Retry игнорируют RequestException
data class RequestSystemException(
    override val message: String,
    val httpStatus: HttpStatus,
) : Exception()