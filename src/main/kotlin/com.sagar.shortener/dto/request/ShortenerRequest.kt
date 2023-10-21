package com.sagar.shortener.dto.request

data class ShortenerRequest(

    @field:com.sagar.shortener.annotation.LongUrl
    val longUrl: String,
    val expiryHits: Long?,
    val expiryTimeInMinutes: Long?
)
