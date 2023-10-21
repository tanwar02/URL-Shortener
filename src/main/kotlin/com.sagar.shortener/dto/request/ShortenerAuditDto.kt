package com.sagar.shortener.dto.request

data class ShortenerAuditDto(
    var shortUrlId: String? = null,
    var generatedFlag: Boolean? = null,
    var status: String? = null,
    var createdTimeStamp: String? = null,
    var identifierType: String? = null,
    var identifierValue: String? = null,
    var sourceIp: String? = null
)
