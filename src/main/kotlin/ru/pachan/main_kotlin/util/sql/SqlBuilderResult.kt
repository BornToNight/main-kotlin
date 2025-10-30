package ru.pachan.main_kotlin.util.sql

data class SqlBuilderResult<T>(
    var data: List<T> = emptyList<T>(),
    var amount: Long = 0,
)
