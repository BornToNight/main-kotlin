package ru.pachan.main_kotlin.model.main

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Schema(description = "Умение")
@Table(name = "skills")
class Skill(

    @Column(nullable = false)
    val name: String,

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    @JsonIgnore
    val persons: Set<Person>? = null,

    @Id
    @SequenceGenerator(
        name = "skills_seq",
        sequenceName = "skills_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skills_seq")
    @Column(name = "skill_id")
    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: Long = 0,
)
