package com.shopping.core.service

import com.shopping.core.model.Cart
import com.shopping.core.model.command.CreateCartCommand
import java.util.UUID

interface CartService {
  fun getById(id: UUID): Cart?
  fun create(createCartCommand: CreateCartCommand): UUID
  fun addProduct(id: UUID, productId: UUID)
  fun addRecipe(id: UUID, recipeId: UUID)
  fun deleteRecipe(id: UUID, recipeId: UUID)
}