package com.shopping.api

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.shopping.AbstractIT
import com.shopping.api.dto.CreateRecipeCommandDto
import com.shopping.api.dto.RecipeDto
import com.shopping.persistence.entities.ProductEntity
import com.shopping.persistence.entities.RecipeEntity
import com.shopping.persistence.entities.RecipeItemEntity
import com.shopping.persistence.repositories.ProductRepository
import com.shopping.persistence.repositories.RecipeRepository
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

private const val INTERNAL_BASE_PATH = "/api/v1/"

class RecipeApiIT(
  private val webTestClient: WebTestClient,
  private val productRepository: ProductRepository,
  private val recipeRepository: RecipeRepository,
) :
  AbstractIT() {

  @Test
  fun getAll() {
    //given
    val product1 = ProductEntity(name = "Banana", priceInCents = 5)
    val product2 = ProductEntity(name = "Apple", priceInCents = 7)
    productRepository.saveAll(listOf(product1, product2))
    val recipe = RecipeEntity(
      name = "Banana Bread",
      items = mutableListOf(RecipeItemEntity(product = product1))
    )
    recipeRepository.save(recipe)

    //when
    val response = webTestClient.get()
      .uri("$INTERNAL_BASE_PATH/recipes")
      .exchange()
      .expectStatus().isOk
      .expectBodyList(RecipeDto::class.java)
      .returnResult()
      .responseBody

    //then
    assertThat(response).isNotNull()
    assertThat(response!!.size).isEqualTo(1)
    assertThat(response[0].id).isNotNull()
    assertThat(response[0].name).isEqualTo("Banana Bread")
    assertThat(response[0].products.size).isEqualTo(1)
    assertThat(response[0].products[0].name).isEqualTo("Banana")
  }

  @Test
  fun create() {
    //given
    val product1 = ProductEntity(name = "Banana", priceInCents = 5)
    val product2 = ProductEntity(name = "Apple", priceInCents = 7)
    productRepository.saveAll(listOf(product1, product2))

    val command = CreateRecipeCommandDto(
      name = "Banana Bread",
      products = listOf(product1.id)
    )

    //when
    val response = webTestClient.post()
      .uri("$INTERNAL_BASE_PATH/recipes")
      .bodyValue(command)
      .exchange()
      .expectStatus().isOk
      .expectBody(UUID::class.java)
      .returnResult()
      .responseBody

    //then
    val recipesInDb = recipeRepository.findAll()
    assertThat(recipesInDb.size).isEqualTo(1)
    assertThat(response).isEqualTo(recipesInDb[0].id)
  }
}