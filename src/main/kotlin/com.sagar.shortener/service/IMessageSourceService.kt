package com.sagar.shortener.service

interface IMessageSourceService {
    fun getMessage(key: String, params: Array<String> = emptyArray()): String
}
