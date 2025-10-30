package ru.pachan.main_kotlin.controller.main.certificate

import graphql.schema.DataFetchingEnvironment
import jakarta.persistence.criteria.Fetch
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.pachan.main_kotlin.dto.main.CertificateGraphQlDto
import ru.pachan.main_kotlin.model.main.Certificate
import ru.pachan.main_kotlin.model.main.Certificate_
import ru.pachan.main_kotlin.model.main.Person
import ru.pachan.main_kotlin.repository.main.CertificateRepository

@Controller
class CertificateGraphQlController(
    private val repository: CertificateRepository,
) {

    @MutationMapping
    fun newCertificate(@Argument certificateGraphQlDto: CertificateGraphQlDto): Certificate {
        return repository.save(Certificate(certificateGraphQlDto.code, null, 0))
    }

    @QueryMapping
    fun certificates(environment: DataFetchingEnvironment): Iterable<Certificate> {
        val s = environment.selectionSet
        return if (s.contains("person")) {
            repository.findAll(fetchPerson())
        } else {
            repository.findAll()
        }
    }

    @QueryMapping
    fun certificate(@Argument id: Int, environment: DataFetchingEnvironment): Certificate {
        var spec = byId(id)
        val selectionSet = environment.selectionSet
        if (selectionSet.contains(Certificate_.person.name)) {
            spec = spec.and(fetchPerson())
        }
        return repository.findOne(spec).orElseThrow { NoSuchElementException() }
    }

    private fun fetchPerson(): Specification<Certificate> {
        return Specification { root, _, _ ->
            val f: Fetch<Certificate, Person> = root.fetch(Certificate_.person, JoinType.LEFT)
            val join = f as Join<Certificate, Person>
            join.on
        }
    }

    private fun byId(id: Int): Specification<Certificate> {
        return Specification { root, _, builder ->
            builder.equal(root.get(Certificate_.id), id)
        }
    }

}