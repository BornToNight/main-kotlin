package ru.pachan.main_kotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class MainKotlinApplication

fun main(args: Array<String>) {
    runApplication<MainKotlinApplication>(*args)
}
