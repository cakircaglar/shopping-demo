package com.shopping.core.model.command

import java.util.UUID

data class CreateCartCommand(
  val products : List<UUID> = emptyList(),
  val recipes : List<UUID> = emptyList()
)
