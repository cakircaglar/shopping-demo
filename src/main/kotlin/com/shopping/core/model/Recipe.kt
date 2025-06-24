package com.shopping.core.model

import java.util.UUID

class Recipe (
  val id: UUID?,
  val name: String,
  val products: List<Product> = emptyList()
)