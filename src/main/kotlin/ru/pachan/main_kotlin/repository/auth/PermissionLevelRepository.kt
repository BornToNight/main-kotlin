package ru.pachan.main_kotlin.repository.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.pachan.main_kotlin.model.auth.PermissionLevel

@Repository
interface PermissionLevelRepository : JpaRepository<PermissionLevel, Long> {

    @Query(
        """
            SELECT pl.permissionLevel
                FROM PermissionLevel pl
                    JOIN pl.rolePermissionPermissionLevels.permission p
                    JOIN pl.rolePermissionPermissionLevels.role r
                WHERE
                    r.id = :roleId
                    AND p.uname = :permissionUname
            """
    )
    fun findPermissionLevelByRoleIdAndPermissionUname(
        @Param("roleId") roleId: Long,
        @Param("permissionUname") permissionUname: String,
    ): Short
}