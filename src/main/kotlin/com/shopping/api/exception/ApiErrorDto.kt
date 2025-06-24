package com.shopping.api.exception

data class ApiErrorDto (
  val code: String,
  val message: String?
)