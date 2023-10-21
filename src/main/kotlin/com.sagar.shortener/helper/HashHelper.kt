package com.sagar.shortener.helper

import com.sagar.shortener.entity.URLDto
import com.sagar.shortener.repository.IShortenerRepository
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDateTime

@Component
class HashHelper(
    private val mongoRepo: IShortenerRepository
) {

    @Value("\${shortener.url.size}")
    lateinit var shortUrlSize: String

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun generateShortUrl(longUrl: String): Mono<String> {

        val digest: ByteArray = MessageDigest.getInstance("MD5").digest(longUrl.toByteArray())
        val base36: String = BigInteger(com.sagar.shortener.constant.AppConstants.BASE36_SIGNUM, digest).toString(com.sagar.shortener.constant.AppConstants.BASE36_RADIX)
        var shortCode = base36.substring(com.sagar.shortener.constant.AppConstants.START_INDEX, shortUrlSize.toInt())

        return findByShortCodeIfNotExpired(shortCode)
            .flatMap {
                logger.info("This short code $shortCode already exists with different long url.")
                shortCode = changeToCapitalLetters(shortCode.toCharArray())
                logger.info("new short code : $shortCode")
                mono { shortCode }
            }
            .flatMap {
                findByShortCodeIfNotExpired(shortCode)
            }
            .flatMap {
                logger.info("This short code $shortCode already exists with different long url.")
                shortCode = changeRandomLetterToCapitalLetter(shortCode.toCharArray())
                logger.info("new short code : $shortCode")
                mono { shortCode }
            }
            .switchIfEmpty {
                mono { shortCode }
            }
    }

    private fun changeToCapitalLetters(shortCode: CharArray): String {

        for (i in shortCode.indices) {
            if (Character.isLowerCase(shortCode[i]))
                shortCode[i] = Character.toUpperCase(shortCode[i])
        }
        return shortCode.joinToString("")
    }

    private fun changeRandomLetterToCapitalLetter(shortCode: CharArray): String {

        val letters = mutableListOf<Char>()
        for (i in shortCode.indices) {
            shortCode[i] = Character.toLowerCase(shortCode[i])
            if (Character.isLetter(shortCode[i]))
                letters.add(shortCode[i])
        }
        val random = letters[(Math.random() * (letters.size - 1)).toInt()]
        for (i in shortCode.indices) {
            if (shortCode[i] == random)
                shortCode[i] = Character.toUpperCase(shortCode[i])
        }
        return shortCode.joinToString("")
    }

    private fun checkIfExpired(urlDto: URLDto): Boolean {

        if (urlDto.expiryDateTime != null && urlDto.expiryDateTime!! < LocalDateTime.now()) {
            return true
        } else if (urlDto.expiryHits != null && urlDto.currHits >= urlDto.expiryHits!!) {
            return true
        }
        return false
    }

    private fun findByShortCodeIfNotExpired(shortCode: String): Mono<URLDto> {

        return mongoRepo.findTopByShortUrlOrderByCreateDateTimeDesc(shortCode)
            .flatMap {
                if (checkIfExpired(it)) {
                    Mono.empty()
                } else {
                    mono { it }
                }
            }
    }
}
