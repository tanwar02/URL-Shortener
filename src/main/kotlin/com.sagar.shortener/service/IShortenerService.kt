package com.sagar.shortener.service

import com.sagar.shortener.dto.request.ShortenerRequest
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono

interface IShortenerService {

    fun generateShortURL(request: ShortenerRequest, headers: HttpHeaders): Mono<String>

    fun getLongURL(shortUrl: String, headers: HttpHeaders): Mono<String>
}
