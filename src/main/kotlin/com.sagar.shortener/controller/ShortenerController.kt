package com.sagar.shortener.controller

import com.sagar.shortener.controller.docs.IShortenerControllerDoc
import com.sagar.shortener.dto.request.ShortenerRequest
import com.sagar.shortener.dto.response.ShortenerResponse
import com.sagar.shortener.service.impl.ShortenerServiceImpl
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.net.URI

@RestController
class ShortenerController(
    private val shortenerServiceImpl: ShortenerServiceImpl
) : IShortenerControllerDoc {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun postGenerateShortURL(request: ShortenerRequest, headers: HttpHeaders): Mono<ShortenerResponse> {
        logger.info("Received Request to generate short url for : $request")
        return shortenerServiceImpl.generateShortURL(request, headers)
            .flatMap {
                mono { ShortenerResponse(it) }
            }
    }

    override fun redirectToLongURL(shortUrl: String, headers: HttpHeaders): Mono<ResponseEntity<URI>> {
        logger.info("Searching long url corresponding to short url $shortUrl")
        return shortenerServiceImpl.getLongURL(shortUrl, headers)
            .flatMap<ResponseEntity<URI>?> {
                Mono.just(
                    ResponseEntity
                        .status(HttpStatus.TEMPORARY_REDIRECT)
                        .location(URI.create(it))
                        .build()
                )
            }.doOnNext {
                logger.info(it.toString())
            }
    }
}
