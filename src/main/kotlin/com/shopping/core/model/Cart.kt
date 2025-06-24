package com.shopping.core.model

import java.util.UUID

data class Cart(
  val id: UUID = UUID.randomUUID(),
  val products : List<Product> = emptyList(),
  val recipes : List<Recipe> = emptyList(),
  val totalInCents : Int
)