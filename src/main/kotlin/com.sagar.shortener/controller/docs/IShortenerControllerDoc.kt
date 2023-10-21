package com.sagar.shortener.controller.docs

import com.sagar.shortener.dto.request.ShortenerRequest
import com.sagar.shortener.dto.response.ShortenerResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import reactor.core.publisher.Mono
import java.net.URI

interface IShortenerControllerDoc {

    @PostMapping("/short_url")
    fun postGenerateShortURL(@Valid @RequestBody request: ShortenerRequest, @RequestHeader headers: HttpHeaders): Mono<ShortenerResponse>

    @GetMapping("/{shortUrl}")
    fun redirectToLongURL(@PathVariable shortUrl: String, @RequestHeader headers: HttpHeaders): Mono<ResponseEntity<URI>>
}
