package ru.pachan.main_kotlin.config

import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "9s")
class SchedulerConfiguration {

// EXPLAIN_V Для постгри
//    @Bean
//    fun lockProvider(dataSource: DataSource): LockProvider {
//        return JdbcTemplateLockProvider(dataSource)
//    }

    @Bean
    fun lockProvider(lettuceConnectionFactory: LettuceConnectionFactory): LockProvider {
        return RedisLockProvider(lettuceConnectionFactory)
    }

}