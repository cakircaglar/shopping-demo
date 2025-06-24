package com.shopping.service

import com.shopping.core.exception.NotFoundException
import com.shopping.core.model.Cart
import com.shopping.core.model.command.CreateCartCommand
import com.shopping.core.persistence.CartPersistencePort
import com.shopping.core.service.CartService
import com.shopping.core.service.ProductService
import com.shopping.core.service.RecipeService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CartServiceImpl(
  private val cartPersistencePort: CartPersistencePort,
  private val productService: ProductService,
  private val recipeService: RecipeService,
) : CartService {
  override fun getById(id: UUID): Cart? {
    return cartPersistencePort.getById(id)
  }

  override fun create(createCartCommand: CreateCartCommand): UUID {
    validateItems(createCartCommand)
    return cartPersistencePort.create(createCartCommand)
  }

  override fun addProduct(id: UUID, productId: UUID) {
    if (cartPersistencePort.getById(id) == null) {
      throw NotFoundException("Cart with id=$id not found!")
    }

    if (!productService.existsById(productId)) {
      throw NotFoundException("Product with id=$productId not found!")
    }

    cartPersistencePort.addProduct(id, productId)
  }

  override fun addRecipe(id: UUID, recipeId: UUID) {
    if (cartPersistencePort.getById(id) == null) {
      throw NotFoundException("Cart with id=$id not found!")
    }

    if (!recipeService.existsById(recipeId)) {
      throw NotFoundException("Recipe with id=$recipeId not found")
    }

    cartPersistencePort.addRecipe(id, recipeId)
  }

  override fun deleteRecipe(id: UUID, recipeId: UUID) {
    val cart = cartPersistencePort.getById(id) ?: throw NotFoundException("Cart with id=$id not found!")

    if (cart.recipes.find { it.id == recipeId } == null) {
      throw NotFoundException("Cart $id do not have such recipe with id $recipeId")
    }

    cartPersistencePort.deleteRecipe(id, recipeId)
  }

  private fun validateItems(createCartCommand: CreateCartCommand) {
    for (productId in createCartCommand.products) {
      if (!productService.existsById(productId)) {
        throw NotFoundException("Product with id=$productId does not exist")
      }
    }
    for (recipeId in createCartCommand.recipes) {
      if (!recipeService.existsById(recipeId)) {
        throw NotFoundException("Recipe with id=$recipeId does not exist")
      }
    }
  }
}