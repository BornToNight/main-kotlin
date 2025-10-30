package ru.pachan.main_kotlin.dto.main.organization

import java.io.Serializable

data class PersonOrganizationDto(
    val id: Long,
    val firstName: String,
//    val surname: String,
) : Serializable
