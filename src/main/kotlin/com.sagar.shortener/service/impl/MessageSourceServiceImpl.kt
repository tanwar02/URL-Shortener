package com.sagar.shortener.service.impl

import com.sagar.shortener.service.IMessageSourceService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class MessageSourceServiceImpl(
    private val messageSource: MessageSource
) : IMessageSourceService {

    override fun getMessage(key: String, params: Array<String>): String {
        return messageSource.getMessage(key, params, Locale.ENGLISH)
    }
}
