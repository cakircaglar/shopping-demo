package com.shopping.core.persistence

import com.shopping.core.model.Recipe
import com.shopping.core.model.command.CreateRecipeCommand
import java.util.UUID

interface RecipePersistencePort {
  fun create(createRecipeCommand: CreateRecipeCommand): UUID
  fun getAll(): List<Recipe>
  fun existsById(id: UUID): Boolean
}