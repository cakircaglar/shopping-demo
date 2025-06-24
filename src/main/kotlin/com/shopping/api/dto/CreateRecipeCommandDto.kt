package com.shopping.api.dto

import java.util.UUID

data class CreateRecipeCommandDto (
  val name: String,
  val products : List<UUID> = emptyList()
)