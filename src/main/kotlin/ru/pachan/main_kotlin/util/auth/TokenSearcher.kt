package ru.pachan.main_kotlin.util.auth

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Component
import ru.pachan.main_kotlin.exception.data.RequestException
import ru.pachan.main_kotlin.repository.auth.UserRepository
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.EMPTY_TOKEN_FIELD
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.EXPIRED_OR_INVALID_TOKEN
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.USER_IS_MISSING
import java.util.*

@Component
class TokenSearcher(
    private val userRepository: UserRepository,
) {

    companion object {
        const val ADMIN = "admin"
    }

    fun isAdmin(token: String): Boolean {
        val userId = getPayloadField(token, "userId").toLong()
        val roleName =
            (userRepository.findByIdOrNull(userId) ?: throw RequestException(
                USER_IS_MISSING.message,
                UNAUTHORIZED
            )).role.name

        return roleName != ADMIN
    }

    fun isOriginalUser(token: String, userId: Long): Boolean {
        val tokenUserId = getPayloadField(token, "userId").toLong()
        return tokenUserId == userId
    }

    fun getPayloadField(token: String, fieldName: String): String {
        val payload = getPayload(token)
        return try {
            ObjectMapper().readTree(payload).get(fieldName).asText()
        } catch (_: Throwable) {
            throw RequestException(EMPTY_TOKEN_FIELD.message, UNAUTHORIZED)
        }
    }

    fun getPayload(token: String): String {
        return try {
            String(Base64.getDecoder().decode(token.split(".")[1]))
        } catch (_: IllegalArgumentException) {
            throw RequestException(EXPIRED_OR_INVALID_TOKEN.message, INTERNAL_SERVER_ERROR)
        }
    }

}
