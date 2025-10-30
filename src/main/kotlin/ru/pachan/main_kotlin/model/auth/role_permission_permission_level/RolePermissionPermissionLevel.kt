package ru.pachan.main_kotlin.model.auth.role_permission_permission_level

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import ru.pachan.main_kotlin.model.auth.Permission
import ru.pachan.main_kotlin.model.auth.PermissionLevel
import ru.pachan.main_kotlin.model.auth.Role

@Entity
@Table(name = "roles_permissions_permission_levels")
class RolePermissionPermissionLevel(

    @EmbeddedId
    val id: RolePermissionPermissionLevelId,

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    val role: Role,

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    val permission: Permission,

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("permissionLevelId")
    @JoinColumn(name = "permission_level_id")
    val permissionLevel: PermissionLevel,
)