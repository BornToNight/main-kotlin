package ru.pachan.main_kotlin.dto.main.organization

import java.io.Serializable

data class OrganizationDto(
    val id: Long,
    val name: String,
    val person: Set<PersonOrganizationDto>,
) : Serializable
