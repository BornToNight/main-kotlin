package ru.pachan.main_kotlin.repository.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.pachan.main_kotlin.model.auth.RefreshToken

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    fun findByRefreshToken(token: String): RefreshToken?

}