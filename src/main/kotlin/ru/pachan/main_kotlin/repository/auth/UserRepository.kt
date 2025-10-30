package ru.pachan.main_kotlin.repository.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.pachan.main_kotlin.model.auth.User

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByLogin(login: String): User?

}