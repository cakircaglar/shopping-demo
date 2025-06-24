package com.shopping.core.model.command

import java.util.UUID

data class CreateRecipeCommand (
  val name: String,
  val products : List<UUID> = emptyList()
)