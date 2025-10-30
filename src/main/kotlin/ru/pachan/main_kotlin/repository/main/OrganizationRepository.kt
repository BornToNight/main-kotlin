package ru.pachan.main_kotlin.repository.main

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.pachan.main_kotlin.model.main.Organization

@Repository
interface OrganizationRepository : JpaRepository<Organization, Long> {

    // v1
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = ["persons"])
    fun findAllWithEntityGraphBy(pageable: Pageable): Page<Organization>

    // v2
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "organization_entity-graph")
    fun findAllWithEntityGraph2By(pageable: Pageable): Page<Organization>

}