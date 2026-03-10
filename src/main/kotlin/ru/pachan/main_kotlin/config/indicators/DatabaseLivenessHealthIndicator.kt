package ru.pachan.main_kotlin.config.indicators

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import ru.pachan.main_kotlin.util.sql.DatabaseConnectionChecker
import java.time.Duration
import java.time.Instant
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Component
class DatabaseLivenessHealthIndicator(
    private val connectionChecker: DatabaseConnectionChecker,
) : HealthIndicator {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${application.health-indicators.database-liveness-threshold}")
    private lateinit var livenessThreshold: Duration

    private var problemStartTime: Instant? = null

    private val lock = ReentrantLock()

    override fun health(): Health {
        lock.withLock {
            val now = Instant.now()
            return when {
                connectionChecker.isDatabaseConnected() -> handleConnectionExists(now)
                isProblemBegin() -> handleFirstFailure(now)
                else -> handleOngoingFailure(now)
            }
        }
    }

    private fun isProblemBegin(): Boolean {
        return problemStartTime == null
    }

    private fun handleConnectionExists(now: Instant): Health {
        if (isConnectionRestored()) {
            handleConnectionRestored(now)
        }
        return Health.up()
            .withDetail("database", "connected")
            .build()
    }

    private fun isConnectionRestored(): Boolean {
        return problemStartTime != null
    }

    private fun handleConnectionRestored(now: Instant) {
        logger.info(
            "Database connection restored after ${
                Duration.between(problemStartTime, now).toMinutes()
            } minutes"
        )
        problemStartTime = null
    }

    private fun handleFirstFailure(now: Instant): Health {
        problemStartTime = now
        logger.warn("Database connection lost, starting timer for liveness")
        return Health.up()
            .withDetail("database", "disconnected")
            .withDetail("problem_started", now.toString())
            .withDetail("problem_duration_minutes", 0)
            .withDetail("remaining_tolerance_minutes", livenessThreshold.toMinutes())
            .withDetail("liveness_threshold_minutes", livenessThreshold.toMinutes())
            .withDetail("status", "tolerating")
            .build()
    }

    private fun handleOngoingFailure(now: Instant): Health {
        val minutesDown = Duration.between(problemStartTime, now).toMinutes()

        return if (isTolerable(minutesDown)) {
            Health.up()
                .withDetail("database", "disconnected")
                .withDetail("problem_started", problemStartTime.toString())
                .withDetail("problem_duration_minutes", minutesDown)
                .withDetail("remaining_tolerance_minutes", livenessThreshold.toMinutes() - minutesDown)
                .withDetail("liveness_threshold_minutes", livenessThreshold.toMinutes())
                .withDetail("status", "tolerating")
                .build()
        } else {
            logger.error("Database unavailable for $minutesDown minutes, marking liveness as DOWN")
            Health.down()
                .withDetail("database", "disconnected")
                .withDetail("problem_started", problemStartTime.toString())
                .withDetail("problem_duration_minutes", minutesDown)
                .withDetail("liveness_threshold_minutes", livenessThreshold.toMinutes())
                .withDetail("action", "Container will be restarted")
                .build()
        }
    }

    private fun isTolerable(minutesDown: Long): Boolean {
        return minutesDown < livenessThreshold.toMinutes()
    }

}