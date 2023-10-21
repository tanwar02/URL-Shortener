package com.sagar.shortener.dto.exception

data class ValidationErrorResponseDto(

    val errors: MutableList<ExceptionDto>
)
