package ru.pachan.main_kotlin.repository.main.person

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.pachan.main_kotlin.dto.main.PersonDto
import ru.pachan.main_kotlin.model.main.Person

@Repository
interface PersonRepository : JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

    @Query(
        """
            SELECT new ru.pachan.main_kotlin.dto.main.PersonDto(p.id, p.firstName, p.organization.name)
            FROM Person p
               WHERE
               (LOWER(firstName) LIKE CONCAT('%', LOWER(:firstName), '%') OR :firstName IS NULL)
               AND (firstName IN (:firstNames) OR :firstNames IS NULL )
            """
    )
    fun findAllPersonsDTOWithFilters(
        @Param("firstName") firstName: String?,
        @Param("firstNames") firstNames: List<String>?,
        pageable: Pageable,
    ): Page<PersonDto>

}