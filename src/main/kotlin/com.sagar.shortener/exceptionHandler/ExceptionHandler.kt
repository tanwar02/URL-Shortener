package com.sagar.shortener.exceptionHandler

import com.sagar.shortener.dto.exception.ExceptionDto
import com.sagar.shortener.dto.exception.ValidationErrorResponseDto
import com.sagar.shortener.exception.CustomException
import com.sagar.shortener.service.IMessageSourceService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.support.WebExchangeBindException
import java.util.stream.Collectors

@ControllerAdvice
class ExceptionHandler(
    private val messageSourceService: IMessageSourceService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${max.longUrl.length}")
    private var maxLongUrlLength: Int? = null

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(customException: CustomException): ResponseEntity<Any> {

        logger.info("Received Request to handle exception.")
        return ResponseEntity(
            ExceptionDto(
                customException.code,
                messageSourceService.getMessage("${customException.code}.MESSAGE"),
                messageSourceService.getMessage(("${customException.code}.DESCRIPTION"), arrayOf(customException.value)),
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(WebExchangeBindException::class)
    @ResponseBody
    fun processUnmergeException(ex: WebExchangeBindException): ResponseEntity<Any>? {

        val list = ex.bindingResult.allErrors.stream()
            .map { fieldError: ObjectError ->
                var message = ""
                var description = ""
                if (fieldError.code.equals("LongUrl")) {
                    message = messageSourceService.getMessage("${messageSourceService.getMessage(fieldError.code!!)}.MESSAGE")
                    description = messageSourceService.getMessage("${messageSourceService.getMessage(fieldError.code!!)}.DESCRIPTION", arrayOf(maxLongUrlLength!!.toString()))
                }
                ExceptionDto(
                    messageSourceService.getMessage(fieldError.code!!),
                    message,
                    description
                )
            }
            .collect(Collectors.toList())
        return ResponseEntity(ValidationErrorResponseDto(list), HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
