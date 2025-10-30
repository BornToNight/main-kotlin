package ru.pachan.main_kotlin.repository.main.person

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.pachan.main_kotlin.dto.dictionary.PaginatedResponse
import ru.pachan.main_kotlin.model.main.PersonQueryBuilder
import ru.pachan.main_kotlin.util.sql.SqlQueryBuilder
import java.sql.ResultSet

@Repository
class PersonDao(
    jdbcTemplate: NamedParameterJdbcTemplate,
) {

    companion object {

        private const val BASE_QUERY =
            "SELECT {} FROM persons WHERE true"

        private const val FIRST_NAME_QUERY =
            " AND (LOWER(first_Name) LIKE CONCAT('%', LOWER(:firstName), '%'))"

        private const val FIRST_NAMES_QUERY =
            " AND (first_Name IN (:firstNames))"

        private val ROW_MAPPER: (ResultSet, Int) -> PersonQueryBuilder = { rs, rowNum ->
            PersonQueryBuilder(
                id = rs.getLong("person_id"),
                firstName = rs.getString("first_name"),
                surname = rs.getString("surname"),
            )
        }

    }

    private val sqlQueryBuilder: SqlQueryBuilder<PersonQueryBuilder> =
        SqlQueryBuilder(
            BASE_QUERY,
            mapOf(
                "firstName" to FIRST_NAME_QUERY,
                "firstNames" to FIRST_NAMES_QUERY
            ),
            ROW_MAPPER,
            jdbcTemplate,
        )

    fun getPersons(firstName: String, firstNames: List<String>): PaginatedResponse<PersonQueryBuilder> {
        val result = sqlQueryBuilder.execute(
            parameters = mapOf(
                "firstName" to firstName,
                "firstNames" to firstNames,
            ),
            limit = 1,
            order = "personId",
            fetchData = true
        )
        return PaginatedResponse(result.amount, result.data)
    }


}