package com.sagar.shortener.service.impl

import com.sagar.shortener.dto.request.ShortenerRequest
import com.sagar.shortener.entity.URLDto
import com.sagar.shortener.exception.CustomException
import com.sagar.shortener.helper.HashHelper
import com.sagar.shortener.helper.ValidationHelper
import com.sagar.shortener.repository.IShortenerRepository
import com.sagar.shortener.service.IShortenerService
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime


@Service
@SuppressWarnings("LongMethod")
class ShortenerServiceImpl(
    private val repoReactive: IShortenerRepository,
    private val hashHelper: HashHelper,
    private val validationHelper: ValidationHelper
) : IShortenerService {

    @Value("\${shortener.base.url}")
    lateinit var baseUrl: String

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun generateShortURL(request: ShortenerRequest, headers: HttpHeaders): Mono<String> {

        validationHelper.validateRequest(request)

        return repoReactive.findTopByLongUrlOrderByCreateDateTimeDesc(request.longUrl)
            .flatMap {
                if (checkIfExpired(it)) {
                    Mono.empty()
                } else {
                    mono { it }
                }
            }
            .flatMap {
                logger.info("Received long url already exists.")
                it.expiryDateTime = LocalDateTime.now()
                logger.info("older url expired and created new one.")
                repoReactive.save(it).thenReturn(it.shortUrl)
            }
            .switchIfEmpty {
                hashHelper.generateShortUrl(request.longUrl)
            }
            .flatMap { shortCode ->
                logger.info("creating the dto to save in the db.")
                var expiryDateTime: LocalDateTime? = null
                if (request.expiryTimeInMinutes != null) {
                    expiryDateTime = LocalDateTime.now().plusMinutes(request.expiryTimeInMinutes!!)
                }
                mono {
                    URLDto(
                        longUrl = request.longUrl,
                        shortUrl = shortCode,
                        currHits = 0,
                        expiryHits = request.expiryHits,
                        createDateTime = LocalDateTime.now(),
                        expiryDateTime = expiryDateTime
                    )
                }
            }
            .flatMap {
                repoReactive.save(it)
            }
            .doOnNext {
                logger.info("data saved in db : $it")
            }
            .flatMap {
                val completeShortUrl = baseUrl + it.shortUrl
                logger.info("complete Short Code : $completeShortUrl")
                mono { completeShortUrl }
            }
    }

    override fun getLongURL(shortUrl: String, headers: HttpHeaders): Mono<String> {

        val completeUrl = baseUrl + shortUrl

        return repoReactive.findTopByShortUrlOrderByCreateDateTimeDesc(shortUrl)
            .switchIfEmpty {
                logger.info("$completeUrl does not exists.")
                throw CustomException(code = "URL-SS-001", value = "$completeUrl")
            }
            .flatMap {
                if (checkIfExpired(it)) {
                    logger.info("$completeUrl is expired.")
                    throw CustomException(code = "URL-SS-001", value = "$completeUrl")
                } else {
                    mono { it }
                }
            }
            .flatMap {
                it.currHits++
                logger.info("updated hits in db : ${it.currHits}")
                repoReactive.save(it)
            }
            .flatMap {
                Mono.just(it.longUrl)
            }
    }

    private fun checkIfExpired(urlDto: URLDto): Boolean {

        if (urlDto.expiryDateTime != null && urlDto.expiryDateTime!! < LocalDateTime.now()) {
            return true
        } else if (urlDto.expiryHits != null && urlDto.currHits >= urlDto.expiryHits!!) {
            return true
        }
        return false
    }
}
