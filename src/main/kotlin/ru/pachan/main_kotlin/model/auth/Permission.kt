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
@Schema(description = "Права пользователя")
@Table(name = "permissions")
class Permission(

    @Column(nullable = false, unique = true)
    val uname: String,

    @Column(nullable = false)
    val description: String,

    @OneToMany(mappedBy = "permission", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    val rolePermissionPermissionLevels: List<RolePermissionPermissionLevel>? = null,

    @Id
    @SequenceGenerator(
        name = "permissions_seq",
        sequenceName = "permissions_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permissions_seq")
    @Column(name = "permission_id")
    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: Long = 0,
)
