package ru.pachan.main_kotlin.model.main

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.io.Serializable

@Entity
@Table(name = "persons")
class PersonQueryBuilder(

    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val surname: String,

    @Id
    @SequenceGenerator(
        name = "persons_seq",
        sequenceName = "persons_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persons_seq")
    @Column(name = "person_id")
    val id: Long = 0,
) : Serializable
