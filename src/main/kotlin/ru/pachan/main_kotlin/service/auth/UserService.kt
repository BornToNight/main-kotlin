package ru.pachan.main_kotlin.service.auth

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.pachan.main_kotlin.dto.dictionary.PaginatedResponse
import ru.pachan.main_kotlin.exception.data.RequestException
import ru.pachan.main_kotlin.model.auth.RefreshToken
import ru.pachan.main_kotlin.model.auth.User
import ru.pachan.main_kotlin.repository.auth.RefreshTokenRepository
import ru.pachan.main_kotlin.repository.auth.UserRepository
import ru.pachan.main_kotlin.util.auth.TokenSearcher
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.ATTEMPT_TO_BYPASS_ACCESS
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.OBJECT_NOT_FOUND
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.PERMISSION_DENIED
import ru.pachan.main_kotlin.util.enums.ExceptionEnum.REQUIRED_FIELDS_EMPTY
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val repository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenSearcher: TokenSearcher,
) {

    fun createOne(user: User): User {
        if (user.password.isBlank()) {
            throw RequestException(REQUIRED_FIELDS_EMPTY.message, BAD_REQUEST)
        }

        user.password = BCryptPasswordEncoder().encode(user.password)
        val savedUser = repository.save(user)

        refreshTokenRepository.save(
            RefreshToken(
                refreshToken = "emptyToken",
                user = savedUser
            )
        )

        return savedUser
    }

    fun getAll(pageable: Pageable): PaginatedResponse<User> {
        val result: Page<User> = repository.findAll(pageable)
        return PaginatedResponse(result.totalElements, result.content)
    }

    fun getOne(id: Long, token: String): User {
        if (tokenSearcher.isAdmin(token) || tokenSearcher.isOriginalUser(token, id)) {
            val result =
                repository.findById(id).getOrNull() ?: throw RequestException(OBJECT_NOT_FOUND.message, NOT_FOUND)
            result.password = ""
            return result
        } else {
            throw RequestException(ATTEMPT_TO_BYPASS_ACCESS.message, FORBIDDEN)
        }
    }

    fun updateOne(id: Long, token: String, user: User): User {
        user.id = id
        val old = repository.findById(id).getOrNull() ?: throw RequestException(
            OBJECT_NOT_FOUND.message,
            NOT_FOUND
        )

        if (user.login == "admin") {
            throw RequestException(PERMISSION_DENIED.message, FORBIDDEN)
        }

        return when {
            tokenSearcher.isAdmin(token) -> {
                user.password = if (user.password.isNotBlank()) {
                    BCryptPasswordEncoder().encode(user.password)
                } else {
                    old.password
                }
                repository.save(user)
                user
            }

            tokenSearcher.isOriginalUser(token, id) -> old
            else -> throw RequestException(ATTEMPT_TO_BYPASS_ACCESS.message, FORBIDDEN)
        }
    }

    fun deleteOne(id: Long) {
        val user = repository.findById(id).getOrNull() ?: return
        if (user.login == "admin") {
            throw RequestException(PERMISSION_DENIED.message, FORBIDDEN)
        } else {
            repository.deleteById(user.id)
        }
    }

}