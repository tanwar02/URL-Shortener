package com.sagar.shortener.repository

import com.sagar.shortener.entity.URLDto
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface IShortenerRepository : ReactiveMongoRepository<URLDto, String> {

//    @Query("{ 'shortUrl': ?0, 'expiryDateTime': { \$gte: ?1 }, \$expr: { \$lt: ['\$currHits', '\$expiryHits'] } }")
//    fun findByShortUrl(shortUrl: String, currentDateTime: LocalDateTime): Mono<URLDto>
//
//    @Query("{ 'longUrl': ?0, 'expiryDateTime': { \$gte: ?1 }, \$expr: { \$lt: ['\$currHits', '\$expiryHits'] } }")
//    fun findByLongUrl(longUrl: String, currentDateTime: LocalDateTime): Mono<URLDto>

    fun findTopByLongUrlOrderByCreateDateTimeDesc(longUrl: String): Mono<URLDto>

    fun findTopByShortUrlOrderByCreateDateTimeDesc(shortUrl: String): Mono<URLDto>
}
