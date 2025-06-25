package com.shopping.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.shopping.core.model.Product
import com.shopping.core.model.command.CreateProductCommand
import com.shopping.core.persistence.ProductPersistencePort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ProductServiceImplTest {

  @Mock
  private lateinit var productPersistencePort: ProductPersistencePort
  private val productService by lazy {
    ProductServiceImpl(
      productPersistencePort
    )
  }

  @Test
  fun `given empty products in database when getAll is called then return empty list`() {
    // Given
    given(productPersistencePort.getAll()).willReturn(emptyList())

    // When
    val products = productService.getAll()

    // Then
    assertThat(products).isEmpty()
  }

  @Test
  fun `given some products in database when getAll is called then returns products`() {
    // Given
    val product1 = Product(
      id = UUID.randomUUID(),
      name = "Product 1",
      priceInCents = 100,
    )
    val product2 = Product(
      id = UUID.randomUUID(),
      name = "Product 2",
      priceInCents = 100,
    )
    given(productPersistencePort.getAll()).willReturn(listOf(product1, product2))

    // When
    val products = productService.getAll()

    // Then
    assertThat(products.size).isEqualTo(2)
    assertThat(products[0]).isEqualTo(product1)
    assertThat(products[1]).isEqualTo(product2)
  }

  @Test
  fun `given valid create product command when create then returns created product ID`() {
    // Given
    val createProductCommand = CreateProductCommand(
      name = "New Product",
      priceInCents = 200
    )
    val expectedId = UUID.randomUUID()
    given(productPersistencePort.create(createProductCommand)).willReturn(expectedId)

    // When
    val productId = productService.create(createProductCommand)

    // Then
    assertThat(productId).isEqualTo(expectedId)
  }

  @Test
  fun `given existing product ID when getById is called then returns product`() {
    // Given
    val existingProduct = Product(
      id = UUID.randomUUID(),
      name = "Existing Product",
      priceInCents = 300
    )
    given(productPersistencePort.getById(existingProduct.id!!)).willReturn(existingProduct)

    // When
    val product = productService.getById(existingProduct.id!!)

    // Then
    assertThat(product).isEqualTo(existingProduct)
  }

  @Test
  fun `given non-existing product ID when getById is called then returns null`() {
    // Given
    val nonExistingId = UUID.randomUUID()
    given(productPersistencePort.getById(nonExistingId)).willReturn(null)

    // When
    val product = productService.getById(nonExistingId)

    // Then
    assertThat(product).isEqualTo(null)
  }

  @Test
  fun `given existing product ID when existsById is called then returns true`() {
    // Given
    val existingId = UUID.randomUUID()
    given(productPersistencePort.existsById(existingId)).willReturn(true)

    // When
    val exists = productService.existsById(existingId)

    // Then
    assertThat(exists).isEqualTo(true)
  }

  @Test
  fun `given non-existing product ID when existsById is called then returns false`() {
    // Given
    val nonExistingId = UUID.randomUUID()
    given(productPersistencePort.existsById(nonExistingId)).willReturn(false)

    // When
    val exists = productService.existsById(nonExistingId)

    // Then
    assertThat(exists).isEqualTo(false)
  }
}