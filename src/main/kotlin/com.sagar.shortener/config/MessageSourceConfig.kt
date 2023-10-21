package com.sagar.shortener.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource

@Configuration
class MessageSourceConfig {
    @Bean
    fun messageSource(): ResourceBundleMessageSource {

        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasenames("internationalization/errors")
        messageSource.setDefaultEncoding("UTF-8")

        return messageSource
    }
}
