package ru.pachan.main_kotlin.model.auth

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
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Schema(description = "Пользователь")
@Table(name = "users")
class User(

    @Column(nullable = false, unique = true)
    val login: String,

    @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    var password: String,

    @Column(name = "fk_role_id")
    val roleId: Long,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "fk_role_id",
        referencedColumnName = "role_id",
        insertable = false,
        updatable = false
    )
    @JsonIgnore
    val role: Role,

    @OneToOne(mappedBy = "user", optional = false)
    @JsonIgnore
    val refreshToken: RefreshToken,

    @Id
    @SequenceGenerator(
        name = "users_seq",
        sequenceName = "users_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @Column(name = "user_id")
    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var id: Long = 0,
)
