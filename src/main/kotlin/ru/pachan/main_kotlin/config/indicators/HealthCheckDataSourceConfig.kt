package ru.pachan.main_kotlin.config.indicators

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class HealthCheckDataSourceConfig {

    @Bean
    @Qualifier("healthCheckDataSource")
    fun healthCheckDataSource(
        @Value("\${spring.datasource.url}") url: String,
        @Value("\${spring.datasource.username}") username: String,
        @Value("\${spring.datasource.password}") password: String,
    ): DataSource {
        val config = HikariConfig().apply {
            this.jdbcUrl = url
            this.username = username
            this.password = password
            this.connectionTimeout = 3000
            this.validationTimeout = 2000
            this.maximumPoolSize = 1
            this.minimumIdle = 0
            this.idleTimeout = 10000
            this.poolName = "HealthCheckPool"
        }
        return HikariDataSource(config)
    }

}
