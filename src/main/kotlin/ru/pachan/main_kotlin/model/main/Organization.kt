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
import jakarta.persistence.NamedAttributeNode
import jakarta.persistence.NamedEntityGraph
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.io.Serializable

@Entity
@Schema(description = "Организация")
@Table(name = "organizations")
@NamedEntityGraph(name = "organization_entity-graph", attributeNodes = [NamedAttributeNode("persons")])
class Organization(

    @Column(nullable = false)
    var name: String,

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @JsonIgnore
    val persons: Set<Person>? = null,

    @Id
    @SequenceGenerator(
        name = "organizations_seq",
        sequenceName = "organizations_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizations_seq")
    @Column(name = "organization_id")
    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: Long = 0,
) : Serializable
