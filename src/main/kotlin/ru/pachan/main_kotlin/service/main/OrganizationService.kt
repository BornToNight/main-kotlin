package ru.pachan.main_kotlin.service.main

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Service
import ru.pachan.main_kotlin.dto.dictionary.PaginatedResponse
import ru.pachan.main_kotlin.dto.main.organization.OrganizationDto
import ru.pachan.main_kotlin.dto.main.organization.PersonOrganizationDto
import ru.pachan.main_kotlin.exception.data.RequestException
import ru.pachan.main_kotlin.model.main.Organization
import ru.pachan.main_kotlin.repository.main.OrganizationRepository
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.OBJECT_NOT_FOUND

@Service
class OrganizationService(
    private val repository: OrganizationRepository,
) {

    // EXPLAIN_V Возможно обдумать и переделать (Evict) из-за pageable
    @Cacheable(value = ["OrganizationService::getAll"], key = "#pageable")
    fun getAll(pageable: Pageable): PaginatedResponse<Organization> {
        val result = repository.findAll(pageable)
        return PaginatedResponse(result.totalElements, result.content)
    }

    fun getAllWithEntityGraph(pageable: Pageable): PaginatedResponse<OrganizationDto> {
        val result = repository.findAllWithEntityGraphBy(pageable)
        return PaginatedResponse(
            result.totalElements,
            result.content.map { organization ->
                OrganizationDto(
                    id = organization.id,
                    name = organization.name,
                    person = organization.persons?.map { person ->
                        PersonOrganizationDto(
                            id = person.id,
                            firstName = person.firstName
                            // surname = person.surname
                        )
                    }?.toSet() ?: emptySet()
                )
            }
        )
    }

    fun getAllWithEntityGraph2(pageable: Pageable): PaginatedResponse<OrganizationDto> {
        val result = repository.findAllWithEntityGraph2By(pageable)
        return PaginatedResponse(
            result.totalElements,
            result.content.map { organization ->
                OrganizationDto(
                    id = organization.id,
                    name = organization.name,
                    person = organization.persons?.map { person ->
                        PersonOrganizationDto(
                            id = person.id,
                            firstName = person.firstName
                            // person.surname
                        )
                    }?.toSet() ?: emptySet()
                )
            }
        )
    }

    @Cacheable(value = ["OrganizationService::getOne"], key = "#id")
    fun getOne(id: Long): Organization {
        return repository.findByIdOrNull(id) ?: throw RequestException(OBJECT_NOT_FOUND.message, HttpStatus.NOT_FOUND)
    }

    @Caching(
        evict = [CacheEvict(value = ["OrganizationService::getAll"], allEntries = true)]
    )
    fun createOne(organization: Organization): Organization {
        return repository.save(organization)
    }

    @Caching(
        put = [CachePut(value = ["OrganizationService::getOne"], key = "#id")],
        evict = [CacheEvict(value = ["OrganizationService::getAll"], allEntries = true)]
    )
    fun updateOne(id: Long, organization: Organization): Organization {
        val oldOrganization =
            repository.findByIdOrNull(id) ?: throw RequestException(OBJECT_NOT_FOUND.message, UNAUTHORIZED)
        oldOrganization.name = organization.name
        return repository.save(oldOrganization)
    }

    @Caching(
        evict = [
            CacheEvict(value = ["OrganizationService::getOne"], key = "#id"),
            CacheEvict(value = ["OrganizationService::getAll"], allEntries = true)
        ]
    )
    fun deleteOne(id: Long) {
        repository.deleteById(id)
    }
}