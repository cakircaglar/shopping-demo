package com.shopping.persistence

import com.shopping.core.model.Cart
import com.shopping.core.model.command.CreateCartCommand
import com.shopping.core.persistence.CartPersistencePort
import com.shopping.persistence.converter.EntityConverter
import com.shopping.persistence.entities.CartEntity
import com.shopping.persistence.entities.CartProductEntity
import com.shopping.persistence.entities.CartRecipeEntity
import com.shopping.persistence.repositories.CartRepository
import com.shopping.persistence.repositories.ProductRepository
import com.shopping.persistence.repositories.RecipeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class CartPersistenceAdapter(
  private val cartRepository: CartRepository,
  private val productRepository: ProductRepository,
  private val recipeRepository: RecipeRepository,
  private val entityConverter: EntityConverter,
) :
  CartPersistencePort {
  override fun getById(id: UUID): Cart? {
    val cartEntityOptional = cartRepository.findById(id)
    if (cartEntityOptional.isEmpty)
      return null

    return entityConverter.toCartModel(cartEntityOptional.get())
  }

  override fun create(createCartCommand: CreateCartCommand): UUID {
    val products = createCartCommand.products.map { CartProductEntity(product = productRepository.findById(it).get()) }
      .toMutableList()
    val recipes =
      createCartCommand.recipes.map { CartRecipeEntity(recipe = recipeRepository.findById(it).get()) }.toMutableList()
    val cartEntity = CartEntity(
      products = products,
      recipes = recipes,
      totalInCents = products.sumOf { it.product.priceInCents } + recipes.sumOf { it.recipe.totalPrice() }
    )

    return cartRepository.save(cartEntity).id
  }

  override fun addProduct(id: UUID, productId: UUID) {
    val productEntity = productRepository.findById(productId).get() //already checked at service
    val cartEntity = cartRepository.findById(id).get() //already checked at service layer
    cartEntity.products.add(CartProductEntity(product = productEntity))
    cartEntity.totalInCents += productEntity.priceInCents
    cartRepository.save(cartEntity)
  }

  override fun addRecipe(id: UUID, recipeId: UUID) {
    val recipeEntity = recipeRepository.findById(recipeId).get()
    val cartEntity = cartRepository.findById(id).get() //already checked at service layer
    cartEntity.recipes.add(CartRecipeEntity(recipe = recipeEntity))
    cartEntity.totalInCents += recipeEntity.totalPrice()
    cartRepository.save(cartEntity)
  }

  override fun deleteRecipe(id: UUID, recipeId: UUID) {
    val cartEntity = cartRepository.findById(id).get() //already checked at service layer
    val recipeToRemove = cartEntity.recipes.find { it.recipe.id == recipeId }
    cartEntity.totalInCents -= recipeToRemove!!.recipe.totalPrice()
    cartEntity.recipes.remove(recipeToRemove)

    cartRepository.save(cartEntity)
  }

}