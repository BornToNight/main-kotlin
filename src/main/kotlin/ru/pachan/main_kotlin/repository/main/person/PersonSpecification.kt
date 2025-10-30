package ru.pachan.main_kotlin.repository.main.person

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import ru.pachan.main_kotlin.model.main.Person
import ru.pachan.main_kotlin.model.main.Person_


class PersonSpecification(
    private val firstName: String,
) : Specification<Person> {

    override fun toPredicate(
        root: Root<Person>,
        query: CriteriaQuery<*>?,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val predicates = mutableListOf<Predicate>()

        if (firstName.isNotEmpty()) {
            predicates.add(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Person_.firstName)),
                    "%" + firstName.lowercase() + "%"
                )
            )
        }
        return criteriaBuilder.and(*predicates.toTypedArray())
    }

}