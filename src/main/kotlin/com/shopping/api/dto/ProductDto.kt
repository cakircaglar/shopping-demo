package com.shopping.api.dto

import java.util.UUID

class ProductDto (
  val id: UUID?,
  val name: String,
  val priceInCents: Int,
)