package com.shopping.core.model

import java.util.UUID

data class Product(
  val id: UUID?,
  val name: String,
  val priceInCents: Int,
)