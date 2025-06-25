package com.shopping.api

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.shopping.AbstractIT
import com.shopping.api.dto.ProductDto
import com.shopping.persistence.entities.ProductEntity
import com.shopping.persistence.repositories.ProductRepository
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

private const val INTERNAL_BASE_PATH = "/api/v1/"

class ProductApiIT(private val webTestClient: WebTestClient, private val productRepository: ProductRepository) :
  AbstractIT() {

  @Test
  fun getAll() {
    //given
    val product1 = ProductEntity(name = "Banana", priceInCents = 5)
    val product2 = ProductEntity(name = "Apple", priceInCents = 7)
    productRepository.saveAll(listOf(product1, product2))

    //when
    val response = webTestClient.get()
      .uri("${INTERNAL_BASE_PATH}/products")
      .exchange()
      .expectStatus().isOk
      .expectBodyList(ProductDto::class.java)
      .returnResult()
      .responseBody

    //then
    assertThat(response).isNotNull()
    assertThat(response!!.size).isEqualTo(2)
    assertThat(response[0].name).isEqualTo("Banana")
    assertThat(response[0].priceInCents).isEqualTo(5)
    assertThat(response[1].name).isEqualTo("Apple")
    assertThat(response[1].priceInCents).isEqualTo(7)
  }

  @Test
  fun create() {
    //given
    val productDto = ProductDto(id = null, name = "Banana", priceInCents = 5)

    //when
    val response = webTestClient.post()
      .uri("${INTERNAL_BASE_PATH}/products")
      .bodyValue(productDto)
      .exchange()
      .expectStatus().isOk
      .expectStatus().isOk
      .expectBody(UUID::class.java)
      .returnResult()
      .responseBody

    //then
    val productsAtDb = productRepository.findAll()
    assertThat(productsAtDb.size).isEqualTo(1)
    assertThat(response).isEqualTo(productsAtDb[0].id)
  }
}