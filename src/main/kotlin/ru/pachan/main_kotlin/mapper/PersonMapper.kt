package ru.pachan.main_kotlin.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import ru.pachan.main_kotlin.dto.main.PersonNameAndOrgNameDto
import ru.pachan.main_kotlin.dto.main.PersonNameDto
import ru.pachan.main_kotlin.model.main.Person

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface PersonMapper {

    fun toPersonNameDto(person: Person): PersonNameDto

    fun toPersonNameListDto(personList: List<Person>): List<PersonNameDto>

    @Mapping(source = "organization.name", target = "organizationName")
    fun toPersonNameAndOrgNameDto(person: Person): PersonNameAndOrgNameDto

    fun toPersonNameAndOrgNameListDto(personList: List<Person>): List<PersonNameAndOrgNameDto>
}
