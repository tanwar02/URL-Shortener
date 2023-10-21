package com.sagar.shortener.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.beans.factory.annotation.Value


class LongUrlValidator : ConstraintValidator<com.sagar.shortener.annotation.LongUrl, String> {

    @Value("\${max.longUrl.length}")
    private var maxLongUrlLength: Int? = null
    override fun isValid(longUrl: String, context: ConstraintValidatorContext?): Boolean {
        return longUrl.length <= maxLongUrlLength!!
    }
}
