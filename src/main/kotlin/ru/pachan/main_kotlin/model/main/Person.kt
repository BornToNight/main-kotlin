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
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.Digits
import java.io.Serializable
import java.math.BigDecimal
import java.util.Set

@Entity
@Schema(description = "Сотрудник")
@Table(name = "persons")
class Person : Serializable {

    @Column(nullable = false)
    lateinit var firstName: String

    @Column(nullable = false)
    lateinit var surname: String

    @Column
    @field:Digits(integer = 10, fraction = 2)
    @field:Schema(description = "Максимум 10 знаков до и 2 знака после запятой")
    var salaryRub: BigDecimal? = null

    @Column
    var hobby: String? = null

    @Column(name = "fk_organization_id")
    val organizationId: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_organization_id",
        referencedColumnName = "organization_id",
        insertable = false,
        updatable = false
    )
    @JsonIgnore
    val organization: Organization? = null

    @Column(name = "fk_certificate_id", unique = true)
    val certificateId: Long = 0

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_certificate_id",
        referencedColumnName = "certificate_id",
        insertable = false,
        updatable = false
    )
    val certificate: Certificate? = null

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "persons_skills",
        joinColumns = [JoinColumn(name = "person_id", referencedColumnName = "person_id")],
        inverseJoinColumns = [JoinColumn(name = "skill_id", referencedColumnName = "skill_id")]
    )
    val skills: Set<Skill>? = null

    @Id
    @SequenceGenerator(
        name = "persons_seq",
        sequenceName = "persons_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persons_seq")
    @Column(name = "person_id")
    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: Long = 0
}

