package com.shopping.api.dto

import java.util.UUID

data class CreateCartCommandDto (
  val products : List<UUID> = emptyList(),
  val recipes : List<UUID> = emptyList()
)