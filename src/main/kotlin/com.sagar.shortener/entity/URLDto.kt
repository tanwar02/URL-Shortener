package com.sagar.shortener.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


@Document("urlDto")
data class URLDto(
    @Id
    val id: String? = null,
    @Indexed
    val longUrl: String,
    @Indexed
    val shortUrl: String,
    var currHits: Long,
    val expiryHits: Long?,
    val createDateTime: LocalDateTime,
    var expiryDateTime: LocalDateTime?
)
