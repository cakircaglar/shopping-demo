package com.shopping.service

import com.shopping.core.exception.NotFoundException
import com.shopping.core.model.Recipe
import com.shopping.core.model.command.CreateRecipeCommand
import com.shopping.core.persistence.RecipePersistencePort
import com.shopping.core.service.ProductService
import com.shopping.core.service.RecipeService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RecipeServiceImpl(
  private val recipePersistencePort: RecipePersistencePort,
  private val productService: ProductService,
) : RecipeService {
  override fun create(createRecipeCommand: CreateRecipeCommand): UUID {
    validateProducts(createRecipeCommand)
    return recipePersistencePort.create(createRecipeCommand)
  }

  override fun getAll(): List<Recipe> {
    return recipePersistencePort.getAll()
  }

  override fun existsById(id: UUID): Boolean {
    return recipePersistencePort.existsById(id)
  }

  private fun validateProducts(createRecipeCommand: CreateRecipeCommand) {
    for (productId in createRecipeCommand.products) {
      if (!productService.existsById(productId)) {
        throw NotFoundException("Product with id=$productId not found")
      }
    }
  }
}