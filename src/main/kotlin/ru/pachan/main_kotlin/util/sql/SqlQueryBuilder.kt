package ru.pachan.main_kotlin.util.sql

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class SqlQueryBuilder<T>(
    private val baseQuery: String,
    private val conditions: Map<String, String>,
    private val rowMapper: RowMapper<T>,
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) {

    fun execute(
        parameters: Map<String, Any>,
        limit: Long,
        order: String = "",
        orderDirection: OrderDirection = OrderDirection.DESC,
        fetchData: Boolean = false,
    ): SqlBuilderResult<T> {
        val sqlBuilderResult = SqlBuilderResult<T>()
        val queryTemplate = makeQueryTemplate(parameters)
        if (fetchData) {
            sqlBuilderResult.data = jdbcTemplate.query(
                makeResultQuery(queryTemplate, limit, order, orderDirection),
                parameters,
                rowMapper,
            )
        }
        sqlBuilderResult.amount = jdbcTemplate.queryForObject(
            makeCountQuery(queryTemplate),
            parameters,
            Long::class.java,
        ) ?: 0

        return sqlBuilderResult
    }

    private fun makeQueryTemplate(parameters: Map<String, Any>): String {
        val queryBuilder = StringBuilder(baseQuery)
        parameters.forEach {
            val condition = conditions[it.key]
            if (condition != null) {
                queryBuilder.append(condition)
            }
        }
        return queryBuilder.toString()
    }

    private fun makeResultQuery(
        queryTemplate: String,
        limit: Long,
        order: String,
        orderDirection: OrderDirection,
    ): String {
        return queryTemplate.replace("{}", "*") +
            makeOrderByQuery(order, orderDirection) +
            makeLimitQuery(limit)
    }

    private fun makeCountQuery(queryTemplate: String): String {
        return queryTemplate.replace("{}", "count(1)")
    }

    private fun makeLimitQuery(limit: Long): String {
        return " LIMIT " + limit
    }

    private fun makeOrderByQuery(
        order: String,
        orderDirection: OrderDirection,
    ): String {
        if (order.isNotEmpty())
            return " order by " + camelToSnake(order) + " " + orderDirection
        return ""
    }

    private fun camelToSnake(str: String): String {
        val result = StringBuilder()
        val c = str[0]
        result.append(c.lowercaseChar())
        for (i in 1..str.length) {
            val ch = str[i]
            if (Character.isUpperCase(i)) {
                result.append('_')
                result.append(ch.lowercaseChar())
            } else {
                result.append(ch)
            }
        }
        return result.toString()
    }

}