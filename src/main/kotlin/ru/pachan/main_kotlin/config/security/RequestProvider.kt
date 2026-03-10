package ru.pachan.main_kotlin.config.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Component
import ru.pachan.main_kotlin.exception.data.RequestException
import ru.pachan.main_kotlin.repository.auth.PermissionLevelRepository
import ru.pachan.main_kotlin.repository.auth.UserRepository
import ru.pachan.main_kotlin.util.auth.TokenSearcher
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.EXPIRED_OR_INVALID_TOKEN
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.INVALID_PATH
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.INVALID_TOKEN
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.PERMISSION_DENIED
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.SYSTEM_ERROR
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.USER_IS_MISSING
import ru.pachan.main_kotlin.util.enums.PermissionLevelEnum

@Component
class RequestProvider(

    @param:Value("\${jwt.key}")
    private val secretKey: String,

    private val userRepository: UserRepository,
    private val permissionLevelRepository: PermissionLevelRepository,
    private val tokenSearcher: TokenSearcher,

    ) {
    fun resolveToken(request: HttpServletRequest): String {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return ""
    }

    fun validateToken(token: String): Boolean {
        if (token.isEmpty()) {
            throw RequestException(EXPIRED_OR_INVALID_TOKEN.message, UNAUTHORIZED);
        }
        try {
            JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token)
            return true
        } catch (_: JWTVerificationException) {
            throw RequestException(EXPIRED_OR_INVALID_TOKEN.message, UNAUTHORIZED);
        } catch (_: IllegalArgumentException) {
            throw RequestException(SYSTEM_ERROR.message, INTERNAL_SERVER_ERROR);
        }
    }

    fun checkPermission(token: String, httpServletRequest: HttpServletRequest) {
        if (token.isEmpty()) {
            throw RequestException(INVALID_TOKEN.message, FORBIDDEN)
        }
        try {
            val path = httpServletRequest.requestURI.split("/").filter { it.isNotEmpty() }
            if (path[0] == "graphql" || path[0] == "refresh") {
                return
            }
            val permission = getPermission(token, path[2])
            if (httpServletRequest.method == HttpMethod.GET.name() && permission < PermissionLevelEnum.PERMISSION_READ.permissionLevel) {
                throw RequestException(PERMISSION_DENIED.message, FORBIDDEN)
            }
            if (httpServletRequest.method == HttpMethod.POST.name() && permission < PermissionLevelEnum.PERMISSION_WRITE.permissionLevel) {
                throw RequestException(PERMISSION_DENIED.message, FORBIDDEN)
            }
            if (httpServletRequest.method == HttpMethod.PUT.name() && permission < PermissionLevelEnum.PERMISSION_UPDATE.permissionLevel) {
                throw RequestException(PERMISSION_DENIED.message, FORBIDDEN)
            }
            if (httpServletRequest.method == HttpMethod.DELETE.name() && permission < PermissionLevelEnum.PERMISSION_DELETE.permissionLevel) {
                throw RequestException(PERMISSION_DENIED.message, FORBIDDEN)
            }
        } catch (e: NullPointerException) {
            throw RequestException(INVALID_TOKEN.message, FORBIDDEN)
        }
    }

    fun getPermission(token: String, uname: String): Short {
        val payload = tokenSearcher.getPayload(token)
        try {
            return permissionLevelRepository.findPermissionLevelByRoleIdAndPermissionUname(
                ObjectMapper().readTree(payload).get("roleId").longValue(),
                uname,
            )
        } catch (_: Throwable) {
            throw RequestException(SYSTEM_ERROR.message, INTERNAL_SERVER_ERROR)
        }
    }

    fun checkAdmin(token: String, httpServletRequest: HttpServletRequest) {
        val path = httpServletRequest.requestURI.split("/").filter { it -> it.isNotBlank() }
        // < 2 - проверка для graphQL
        if (path.size < 2 || path[1] != "auth") return

        val payload = tokenSearcher.getPayload(token)

        var userId: Long

        try {
            userId = ObjectMapper().readTree(payload).get("userId").asLong()
        } catch (_: NumberFormatException) {
            throw RequestException(EXPIRED_OR_INVALID_TOKEN.message, UNAUTHORIZED)
        } catch (_: Throwable) {
            throw RequestException(SYSTEM_ERROR.message, INTERNAL_SERVER_ERROR)
        }

        try {
            if (
                httpServletRequest.method == HttpMethod.GET.name()
                || httpServletRequest.method == HttpMethod.POST.name()
                || httpServletRequest.method == HttpMethod.PUT.name()
            ) {
                if (
                    path.size == 4
                    && (path[2] == "user" || path[2] == "refresh")
                    || path.size == 3 && path[2] == "refresh"
                ) {
                    return
                }
            }
        } catch (_: NullPointerException) {
            throw RequestException(INVALID_PATH.message, INTERNAL_SERVER_ERROR)
        }


        if ((userRepository.findByIdOrNull(userId) ?: throw RequestException(
                USER_IS_MISSING.message,
                UNAUTHORIZED
            )).role.name != TokenSearcher.ADMIN
        ) {
            throw RequestException(PERMISSION_DENIED.message, FORBIDDEN)
        }
    }

}