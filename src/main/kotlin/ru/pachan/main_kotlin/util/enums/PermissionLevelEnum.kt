package ru.pachan.main_kotlin.util.enums

enum class PermissionLevelEnum(val permissionLevel: Short) {
    PERMISSION_READ(1),
    PERMISSION_WRITE(2),
    PERMISSION_UPDATE(3),
    PERMISSION_DELETE(4),
}