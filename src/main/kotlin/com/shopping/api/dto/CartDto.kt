package com.shopping.api.dto

import java.util.UUID

class CartDto (
  val id: UUID?,
  val products : List<ProductDto> = emptyList(),
  val recipes : List<RecipeDto> = emptyList(),
  val totalInCents : Int
)