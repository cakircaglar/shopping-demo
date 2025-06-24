package com.shopping.core.model.command

data class CreateProductCommand (
  val name: String,
  val priceInCents: Int
)