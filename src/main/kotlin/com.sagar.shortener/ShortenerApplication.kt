package com.sagar.shortener

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories
class ShortenerApplication
fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<ShortenerApplication>(*args)
}
