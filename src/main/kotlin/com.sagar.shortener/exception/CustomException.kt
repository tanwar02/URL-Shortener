package com.sagar.shortener.exception

class CustomException(

    val code: String,
    val value: String

) : RuntimeException()
