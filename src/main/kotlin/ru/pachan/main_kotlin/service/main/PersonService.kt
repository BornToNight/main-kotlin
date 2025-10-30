package ru.pachan.main_kotlin.service.main

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pachan.main_kotlin.dto.dictionary.PaginatedResponse;
import ru.pachan.main_kotlin.dto.main.PersonDto;
import ru.pachan.main_kotlin.dto.main.PersonNameAndOrgNameDto;
import ru.pachan.main_kotlin.dto.main.PersonNameDto;
import ru.pachan.main_kotlin.exception.data.RequestException;
import ru.pachan.main_kotlin.mapper.PersonMapper;
import ru.pachan.main_kotlin.model.main.Person;
import ru.pachan.main_kotlin.model.main.PersonQueryBuilder;
import ru.pachan.main_kotlin.repository.main.person.PersonDao;
import ru.pachan.main_kotlin.repository.main.person.PersonRepository;
import ru.pachan.main_kotlin.repository.main.person.PersonSpecification;
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.OBJECT_NOT_FOUND;

@Service
class PersonService(
    private val repository: PersonRepository,
    private val personDao: PersonDao,
    private val personMapper: PersonMapper,
) {

    @Transactional
    fun getAll(pageable: Pageable, firstName: String?, firstNames: List<String>?): PaginatedResponse<PersonDto> {
        val persons = repository.findAllPersonsDTOWithFilters(firstName, firstNames, pageable)
        return PaginatedResponse(persons.totalElements, persons.content)
    }

    @Transactional
    fun getAllNames(pageable: Pageable): PaginatedResponse<PersonNameDto> {
        val persons = repository.findAll(pageable)
        return PaginatedResponse(persons.totalElements, personMapper.toPersonNameListDto(persons.content))
    }

    @Transactional
    fun getAllNamesAndOrgNames(pageable: Pageable): PaginatedResponse<PersonNameAndOrgNameDto> {
        val persons = repository.findAll(pageable)
        // ATTENTION будет n+1 - энтити граф может исправить ситуацию
        return PaginatedResponse(persons.totalElements, personMapper.toPersonNameAndOrgNameListDto(persons.content))
    }

    fun getAllWithSqlQueryBuilder(
        firstName: String,
        firstNames: List<String>,
    ): PaginatedResponse<PersonQueryBuilder> {
        return personDao.getPersons(firstName, firstNames)
    }

    @Transactional
    fun getAllWithSpecification(pageable: Pageable, firstName: String): PaginatedResponse<PersonDto> {
        val specification = PersonSpecification(firstName)
        val persons = repository.findAll(specification, pageable)
        val result = persons.content.map {
            PersonDto(
                id = it.id,
                firstName = it.firstName,
                organizationName = it.organization?.name ?: ""
            )
        }
        return PaginatedResponse(persons.totalElements, result)
    }

    fun getOne(id: Long): Person {
        return repository.findByIdOrNull(id) ?: throw RequestException(OBJECT_NOT_FOUND.message, HttpStatus.NOT_FOUND)
    }

    fun createOne(person: Person): Person {
        return repository.save(person)
    }

    fun updateOne(id: Long, person: Person): Person {
        val oldPerson = repository.findByIdOrNull(id) ?: throw RequestException(OBJECT_NOT_FOUND.message, UNAUTHORIZED)

        oldPerson.firstName = person.firstName
//        oldPerson.surname = person.surname
        oldPerson.salaryRub = person.salaryRub
        oldPerson.hobby = person.hobby
        return repository.save(oldPerson)
    }

    fun deleteOne(id: Long) {
        repository.deleteById(id)
    }

}