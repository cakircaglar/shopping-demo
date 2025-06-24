package com.shopping.api.dto

import java.util.UUID

data class RecipeDto(
  val id: UUID?,
  val name: String,
  val products: List<ProductDto> = emptyList()
)