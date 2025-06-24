package com.shopping.api.dto

data class CreateProductCommandDto (
  val name: String,
  val priceInCents: Int
)