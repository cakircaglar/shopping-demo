package com.shopping.api.exception

import com.shopping.core.exception.NotFoundException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

private const val ERROR_CODE_INTERNAL_SERVER_ERROR = "internal-server-error"

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

  @ExceptionHandler(value = [NotFoundException::class])
  fun onNotFoundException(ex: NotFoundException, request: WebRequest): ResponseEntity<Any>? {
    return handleExceptionInternal(
      ex, ApiErrorDto(ex.errorCode, ex.message),
      jsonHeader(), HttpStatus.NOT_FOUND, request
    )
  }

  @ExceptionHandler(value = [Exception::class])
  fun onGeneralException(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
    return handleExceptionInternal(
      ex, ApiErrorDto(ERROR_CODE_INTERNAL_SERVER_ERROR, ex.message),
      jsonHeader(), HttpStatus.INTERNAL_SERVER_ERROR, request
    )
  }

  private fun jsonHeader(): HttpHeaders {
    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_JSON
    return headers
  }
}