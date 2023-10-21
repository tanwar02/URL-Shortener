package com.sagar.shortener.helper

import com.sagar.shortener.dto.request.ShortenerRequest
import com.sagar.shortener.exception.CustomException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ValidationHelper {

    @Value("\${max.time.limit}")
    var maxTimeLimit: Long = 0

    @Value("\${max.expiry.hits}")
    var maxExpiryHits: Long = 0

    @Suppress("ThrowsCount")
    fun validateRequest(request: ShortenerRequest) {

        if (request.expiryHits == null && request.expiryTimeInMinutes == null)
            return
        if (request.expiryHits == null || request.expiryTimeInMinutes == null)
            throw CustomException(code = "URL-SS-006", value = "")
        if (request.expiryHits!! <= 0 || request.expiryTimeInMinutes!! <= 0)
            throw CustomException(code = "URL-SS-002", value = "")
        if (request.expiryHits > maxExpiryHits)
            throw CustomException(code = "URL-SS-005", value = "$maxExpiryHits")
        if (request.expiryTimeInMinutes > maxTimeLimit)
            throw CustomException(code = "URL-SS-003", value = "$maxTimeLimit")
    }
}
