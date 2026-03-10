package ru.pachan.main_kotlin.util.sql

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class DatabaseConnectionChecker(
    @param:Qualifier("healthCheckDataSource")
    private val healthCheckDataSource: DataSource,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun isDatabaseConnected(): Boolean {
        return try {
            healthCheckDataSource.connection.use { connection ->
                connection.isValid(1)
            }
        } catch (e: Exception) {
            logger.debug("Database connection check failed", e)
            false
        }
    }

}