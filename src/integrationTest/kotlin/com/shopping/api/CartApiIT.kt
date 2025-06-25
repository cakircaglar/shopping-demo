package com.shopping.api

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.shopping.AbstractIT
import com.shopping.api.dto.CartDto
import com.shopping.core.model.command.CreateCartCommand
import com.shopping.persistence.entities.CartEntity
import com.shopping.persistence.entities.CartProductEntity
import com.shopping.persistence.entities.CartRecipeEntity
import com.shopping.persistence.entities.ProductEntity
import com.shopping.persistence.entities.RecipeEntity
import com.shopping.persistence.entities.RecipeItemEntity
import com.shopping.persistence.repositories.CartRepository
import com.shopping.persistence.repositories.ProductRepository
import com.shopping.persistence.repositories.RecipeRepository
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

private const val INTERNAL_BASE_PATH = "/api/v1/"

class CartApiIT(
  private val webTestClient: WebTestClient,
  private val productRepository: ProductRepository,
  private val recipeRepository: RecipeRepository,
  private val cartRepository: CartRepository,
) :
  AbstractIT() {

  @Test
  fun getById() {
    //given
    val product1 = ProductEntity(name = "Banana", priceInCents = 5)
    val product2 = ProductEntity(name = "Apple", priceInCents = 7)
    productRepository.saveAll(listOf(product1, product2))
    val recipe = RecipeEntity(
      name = "Banana Bread",
      items = mutableListOf(RecipeItemEntity(product = product1))
    )
    recipeRepository.save(recipe)
    val cartEntity = CartEntity(
      products = mutableListOf(
        CartProductEntity(
          product = product2
        )
      ),
      recipes = mutableListOf(
        CartRecipeEntity(
          recipe = recipe
        )
      )
    )
    cartRepository.save(cartEntity)

    //when
    val response = webTestClient.get()
      .uri("$INTERNAL_BASE_PATH/carts/${cartEntity.id}")
      .exchange()
      .expectStatus().isOk
      .expectBody(CartDto::class.java)
      .returnResult()
      .responseBody

    //then
    assertThat(response).isNotNull()
    assertThat(response!!.id).isNotNull()
    assertThat(response.products.size).isEqualTo(1)
    assertThat(response.products[0].name).isEqualTo("Apple")
    assertThat(response.recipes.size).isEqualTo(1)
    assertThat(response.recipes[0].name).isEqualTo("Banana Bread")
  }

  @Test
  fun create() {
    //given
    val product1 = ProductEntity(name = "Banana", priceInCents = 5)
    val product2 = ProductEntity(name = "Apple", priceInCents = 7)
    productRepository.saveAll(listOf(product1, product2))
    val recipe = RecipeEntity(
      name = "Banana Bread",
      items = mutableListOf(RecipeItemEntity(product = product1))
    )
    recipeRepository.save(recipe)

    val command = CreateCartCommand(
      products = listOf(product2.id),
      recipes = listOf(recipe.id)
    )

    //when
    val response = webTestClient.post()
      .uri("$INTERNAL_BASE_PATH/carts")
      .bodyValue(command)
      .exchange()
      .expectStatus().isOk
      .expectBody(UUID::class.java)
      .returnResult()
      .responseBody

    //then
    val cartsInDb = cartRepository.findAll()
    assertThat(cartsInDb.size).isEqualTo(1)
    assertThat(response).isEqualTo(cartsInDb[0].id)
  }

  @Test
  fun addRecipe(){
    //given

    //add products
    val product1 = ProductEntity(name = "Banana", priceInCents = 5)
    val product2 = ProductEntity(name = "Apple", priceInCents = 7)
    val product3 = ProductEntity(name = "Bread", priceInCents = 2)
    productRepository.saveAll(listOf(product1, product2, product3))

    //add cart using only product2
    val cartEntity = CartEntity(
      products = mutableListOf(
        CartProductEntity(
          product = product2
        )
      ),
      totalInCents = product2.priceInCents
    )
    cartRepository.save(cartEntity)

    //create new recipe
    val recipe = RecipeEntity(
      name = "Banana Shake",
      items = mutableListOf(RecipeItemEntity(product = product1))
    )
    recipeRepository.save(recipe)

    //when-then
    webTestClient.post()
      .uri("$INTERNAL_BASE_PATH/carts/${cartEntity.id}/recipes/${recipe.id}")
      .exchange()
      .expectStatus().isOk

    val cart = cartRepository.findById(cartEntity.id)
    assertThat(cart.isPresent).isEqualTo(true)
    assertThat(cart.get().recipes.size).isEqualTo(1)
    assertThat(cart.get().recipes[0].recipe.name).isEqualTo("Banana Shake")
    assertThat(cart.get().totalInCents).isEqualTo(product2.priceInCents + recipe.totalPrice())
  }

  @Test
  fun deleteRecipe(){
    //given
    val product1 = ProductEntity(name = "Banana", priceInCents = 5)
    val product2 = ProductEntity(name = "Apple", priceInCents = 7)
    productRepository.saveAll(listOf(product1, product2))
    val recipe = RecipeEntity(
      name = "Banana Bread",
      items = mutableListOf(RecipeItemEntity(product = product1))
    )
    recipeRepository.save(recipe)
    val cartEntity = CartEntity(
      products = mutableListOf(
        CartProductEntity(
          product = product2
        )
      ),
      recipes = mutableListOf(
        CartRecipeEntity(
          recipe = recipe
        )
      ),
      totalInCents = product2.priceInCents + recipe.totalPrice()
    )
    cartRepository.save(cartEntity)

    //when-then
    webTestClient.delete()
      .uri("$INTERNAL_BASE_PATH/carts/${cartEntity.id}/recipes/${recipe.id}")
      .exchange()
      .expectStatus().isOk

    val cart = cartRepository.findById(cartEntity.id).get()
    assertThat(cart.recipes.size).isEqualTo(0)
    assertThat(cart.products.size).isEqualTo(1)
    assertThat(cart.totalInCents).isEqualTo(7) // Only Apple product remains in the cart
  }
}