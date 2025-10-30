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
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import ru.pachan.main_kotlin.model.auth.role_permission_permission_level.RolePermissionPermissionLevel

@Entity
@Schema(description = "Роли пользователя")
@Table(name = "roles")
class Role(

    @Column(nullable = false, unique = true)
    val name: String,

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    val users: Set<User>? = null,

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    val rolePermissionPermissionLevels: List<RolePermissionPermissionLevel>? = null,

    @Id
    @SequenceGenerator(
        name = "roles_seq",
        sequenceName = "roles_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_seq")
    @Column(name = "role_id")
    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: Long = 0,
)
