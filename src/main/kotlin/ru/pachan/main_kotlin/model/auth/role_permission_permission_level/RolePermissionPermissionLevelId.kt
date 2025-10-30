package ru.pachan.main_kotlin.model.auth.role_permission_permission_level

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
class RolePermissionPermissionLevelId(

    @Column(name = "role_id")
    val roleId: Long,

    @Column(name = "permission_id")
    val permissionId: Long,

    @Column(name = "permission_level_id")
    val permissionLevelId: Long,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false

        other as RolePermissionPermissionLevelId
        return roleId == other.roleId &&
            permissionId == other.permissionId &&
            permissionLevelId == other.permissionLevelId
    }

    override fun hashCode(): Int {
        return Objects.hash(roleId, permissionId, permissionLevelId)
    }
}
