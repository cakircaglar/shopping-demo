package com.shopping.persistence

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.shopping.core.model.Product
import com.shopping.core.model.command.CreateProductCommand
import com.shopping.persistence.converter.EntityConverter
import com.shopping.persistence.entities.ProductEntity
import com.shopping.persistence.repositories.ProductRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ProductPersistenceAdapterTest {
  @Mock
  private lateinit var productRepository: ProductRepository

  @Mock
  private lateinit var entityConverter: EntityConverter

  @Captor
  private lateinit var productEntityCaptor: ArgumentCaptor<ProductEntity>

  private val productPersistenceAdapter by lazy {
    ProductPersistenceAdapter(productRepository, entityConverter)
  }

  @Test
  fun `given products in database when getAll then returns all products`() {
    //given
    val productEntity1 = ProductEntity(
      id = UUID.randomUUID(),
      name = "Test Product 1",
      priceInCents = 1000
    )
    val productEntity2 = ProductEntity(
      id = UUID.randomUUID(),
      name = "Test Product 2",
      priceInCents = 2000
    )
    given(productRepository.findAll()).willReturn(listOf(productEntity1, productEntity2))
    given(entityConverter.toProductModel(productEntity1)).willReturn(
      Product(
        id = productEntity1.id,
        name = productEntity1.name,
        priceInCents = productEntity1.priceInCents
      )
    )
    given(entityConverter.toProductModel(productEntity2)).willReturn(
      Product(
        id = productEntity2.id,
        name = productEntity2.name,
        priceInCents = productEntity2.priceInCents
      )
    )

    //when
    val products = productPersistenceAdapter.getAll()

    //then
    assertThat(products.size).isEqualTo(2)
    assertThat(products[0].name).isEqualTo("Test Product 1")
    assertThat(products[0].priceInCents).isEqualTo(1000)
    assertThat(products[0].id).isEqualTo(productEntity1.id)
    assertThat(products[1].name).isEqualTo("Test Product 2")
    assertThat(products[1].priceInCents).isEqualTo(2000)
    assertThat(products[1].id).isEqualTo(productEntity2.id)
  }

  @Test
  fun `given valida create product command when create then created id will return`() {
    // Given
    val createProductCommand = CreateProductCommand(
      name = "New Product",
      priceInCents = 1500
    )

    given(productRepository.save(productEntityCaptor.capture())).willAnswer { it.getArgument(0) }

    // When
    val createdId = productPersistenceAdapter.create(createProductCommand)

    // Then
    assertThat(createdId).isNotNull()
    productEntityCaptor.value.let {
      assertThat(it.name).isEqualTo(createProductCommand.name)
      assertThat(it.priceInCents).isEqualTo(createProductCommand.priceInCents)
    }
  }

  @Test
  fun `given existing product id when getById then returns product`() {
    // Given
    val productId = UUID.randomUUID()
    val productEntity = ProductEntity(
      id = productId,
      name = "Existing Product",
      priceInCents = 1200
    )
    given(productRepository.findById(productId)).willReturn(java.util.Optional.of(productEntity))
    given(entityConverter.toProductModel(productEntity)).willReturn(
      Product(
        id = productEntity.id,
        name = productEntity.name,
        priceInCents = productEntity.priceInCents
      )
    )

    // When
    val product = productPersistenceAdapter.getById(productId)

    // Then
    assertThat(product).isNotNull()
    assertThat(product?.id).isEqualTo(productId)
    assertThat(product?.name).isEqualTo("Existing Product")
    assertThat(product?.priceInCents).isEqualTo(1200)
  }

  @Test
  fun `given id when existsById then returns true if product exists`() {
    // Given
    val productId = UUID.randomUUID()
    given(productRepository.existsById(productId)).willReturn(true)

    // When
    val exists = productPersistenceAdapter.existsById(productId)

    // Then
    assertThat(exists).isEqualTo(true)
  }

}