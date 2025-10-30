package ru.pachan.main_kotlin

import net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration
import net.devh.boot.grpc.client.autoconfigure.GrpcClientMicrometerTraceAutoConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

// EXPLAIN_V Ломает метрики
@EnableAutoConfiguration(exclude = [
    GrpcClientMetricAutoConfiguration::class,
    GrpcClientMicrometerTraceAutoConfiguration::class
])
@EnableCaching
@SpringBootApplication
class MainKotlinApplication

fun main(args: Array<String>) {
    runApplication<MainKotlinApplication>(*args)
}
