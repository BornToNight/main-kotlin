package ru.pachan.main_kotlin.model.main

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.io.Serializable

@Entity
@Schema(description = "Удостоверение")
@Table(name = "certificates")
class Certificate(

    @Column(nullable = false)
    var code: String,

    @OneToOne(mappedBy = "certificate", optional = false, cascade = [CascadeType.ALL])
    @JsonIgnore
    val person: Person?,

    @Id
    @SequenceGenerator(
        name = "certificates_seq",
        sequenceName = "certificates_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "certificates_seq")
    @Column(name = "certificate_id")
    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: Long = 0,
) : Serializable
