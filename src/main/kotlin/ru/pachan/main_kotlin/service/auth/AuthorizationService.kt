package ru.pachan.main_kotlin.service.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.pachan.main_kotlin.dto.auth.AuthorizationDto
import ru.pachan.main_kotlin.dto.auth.RefreshDataDto
import ru.pachan.main_kotlin.exception.data.RequestException
import ru.pachan.main_kotlin.model.auth.RefreshToken
import ru.pachan.main_kotlin.model.auth.User
import ru.pachan.main_kotlin.repository.auth.RefreshTokenRepository
import ru.pachan.main_kotlin.repository.auth.UserRepository
import ru.pachan.main_kotlin.util.auth.TokenSearcher
import ru.pachan.main_kotlin.util.enums.ExceptionEnum
import java.util.*


@Service
class AuthorizationService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenSearcher: TokenSearcher,
) {

    @Value("\${jwt.refresh-token-expiration}")
    private lateinit var refreshTime: String

    @Value("\${jwt.access-token-expiration}")
    private lateinit var accessTime: String

    @Value("\${jwt.key}")
    private lateinit var key: String


    fun generate(authorizationDto: AuthorizationDto): RefreshDataDto {
        val user = userRepository.findByLogin(authorizationDto.login) ?: throw RequestException(
            ExceptionEnum.OBJECT_NOT_FOUND.message,
            NOT_FOUND
        )

        return if (BCryptPasswordEncoder().matches(authorizationDto.password, user.password)) {
            generateJWT(user = user)
        } else {
            throw RequestException(
                ExceptionEnum.WRONG_LOGIN_OR_PASSWORD.message,
                BAD_REQUEST
            )
        }
    }

    fun refresh(token: String): RefreshDataDto {
        return generateJWT(token = token)
    }

    private fun generateJWT(user: User? = null, token: String? = null): RefreshDataDto {
        var refreshEntry: RefreshToken? = null
        try {
            if (!token.isNullOrBlank()) {
                refreshEntry =
                    refreshTokenRepository.findByRefreshToken(token.split(" ")[1]) ?: throw NullPointerException()
            }
        } catch (_: Throwable) {
            throw RequestException(ExceptionEnum.EXPIRED_OR_INVALID_TOKEN.message, UNAUTHORIZED)
        }

        val foundUser = findUser(token, user)

        val refreshToken = try {
            JWT.create()
                .withClaim("userId", foundUser.id)
                .withIssuedAt(Date())
                .withExpiresAt(Date(System.currentTimeMillis() + refreshTime.toLong()))
                .sign(Algorithm.HMAC256(key))
        } catch (_: NumberFormatException) {
            throw RequestException(ExceptionEnum.INVALID_PROPERTY_TOKEN_ACCESS_TIME.message, INTERNAL_SERVER_ERROR)
        } catch (_: Throwable) {
            throw RequestException(ExceptionEnum.DENIED_TOKEN_CREATE.message, INTERNAL_SERVER_ERROR)
        }

        if (refreshEntry!!.refreshToken.isNotBlank()) {
            try {
                if (JWT.decode(refreshEntry.refreshToken).expiresAt.time < System.currentTimeMillis()) {
                    throw RequestException(ExceptionEnum.EXPIRED_OR_INVALID_TOKEN.message, UNAUTHORIZED)
                } else {
                    refreshEntry.refreshToken = refreshToken
                }
            } catch (_: NumberFormatException) {
                throw RequestException(ExceptionEnum.INVALID_PROPERTY_TOKEN_REFRESH_TIME.message, INTERNAL_SERVER_ERROR)
            } catch (_: Throwable) {
                throw RequestException(ExceptionEnum.INCORRECT_EXPIRATION_DATE.message, UNAUTHORIZED)
            }
        } else {
            refreshEntry.refreshToken = refreshToken
        }

        val newToken = try {
            JWT.create()
                .withClaim("userId", foundUser.id)
                .withClaim("roleId", foundUser.roleId)
                .withIssuedAt(Date())
                .withExpiresAt(Date(System.currentTimeMillis() + accessTime.toLong()))
                .sign(Algorithm.HMAC256(key))
        } catch (_: NumberFormatException) {
            throw RequestException(ExceptionEnum.INVALID_PROPERTY_TOKEN_ACCESS_TIME.message, INTERNAL_SERVER_ERROR)
        } catch (_: Throwable) {
            throw RequestException(ExceptionEnum.DENIED_TOKEN_CREATE.message, INTERNAL_SERVER_ERROR)
        }

        val userRefreshToken = foundUser.refreshToken
        userRefreshToken.refreshToken = refreshEntry.refreshToken
        refreshTokenRepository.save(userRefreshToken)

        return if (user != null) {
            RefreshDataDto(userRefreshToken.refreshToken, newToken, user.roleId, user.id)
        } else {
            RefreshDataDto(userRefreshToken.refreshToken, newToken, 0, 0)
        }
    }

    private fun findUser(token: String?, user: User?): User {
        return user ?: userRepository.findByIdOrNull(
            tokenSearcher.getPayloadField(token ?: "", "userId").toLong()
        ) ?: throw RequestException(ExceptionEnum.USER_IS_MISSING.message, UNAUTHORIZED)
    }

}