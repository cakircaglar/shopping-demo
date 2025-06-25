package com.shopping.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.shopping.core.exception.NotFoundException
import com.shopping.core.model.Cart
import com.shopping.core.model.Recipe
import com.shopping.core.model.command.CreateCartCommand
import com.shopping.core.persistence.CartPersistencePort
import com.shopping.core.service.ProductService
import com.shopping.core.service.RecipeService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class CartServiceImplTest {

  @Mock
  private lateinit var productService: ProductService

  @Mock
  private lateinit var recipeService: RecipeService

  @Mock
  private lateinit var cartPersistencePort: CartPersistencePort

  private val cartService by lazy {
    CartServiceImpl(
      cartPersistencePort,
      productService,
      recipeService
    )
  }

  @Test
  fun `given empty carts in database when getById is called then return null`() {
    // Given
    val cartId = UUID.randomUUID()
    given(cartPersistencePort.getById(cartId)).willReturn(null)

    // When
    val cart = cartService.getById(cartId)

    // Then
    assertThat(cart).isEqualTo(null)
  }

  @Test
  fun `given some carts in database when getById is called then returns cart`() {
    // Given
    val cartId = UUID.randomUUID()
    val expectedCart = Cart(
      id = cartId,
      products = emptyList(),
      recipes = emptyList(),
      totalInCents = 0
    )
    given(cartPersistencePort.getById(cartId)).willReturn(expectedCart)

    // When
    val cart = cartService.getById(cartId)

    // Then
    assertThat(cart).isEqualTo(expectedCart)
  }

  @Test
  fun `given valid create cart command when create then returns created cart's Id`() {
    // Given
    val product1Id = UUID.randomUUID()
    val product2Id = UUID.randomUUID()
    val recipeId = UUID.randomUUID()
    val createCartCommand = CreateCartCommand(
      products = listOf(product1Id, product2Id),
      recipes = listOf(recipeId)
    )
    val expectedCartId = UUID.randomUUID()
    given(productService.existsById(product1Id)).willReturn(true)
    given(productService.existsById(product2Id)).willReturn(true)
    given(recipeService.existsById(recipeId)).willReturn(true)
    given(cartPersistencePort.create(createCartCommand)).willReturn(expectedCartId)

    // When
    val cartId = cartService.create(createCartCommand)

    // Then
    assertThat(cartId).isEqualTo(expectedCartId)
  }

  @Test
  fun `given create cart command with non-existing product when create then throws NotFoundException`() {
    // Given
    val product1Id = UUID.randomUUID()
    val product2Id = UUID.randomUUID()
    val recipeId = UUID.randomUUID()
    val createCartCommand = CreateCartCommand(
      products = listOf(product1Id, product2Id),
      recipes = listOf(recipeId)
    )
    given(productService.existsById(product1Id)).willReturn(true)
    given(productService.existsById(product2Id)).willReturn(false)

    // When - Then
    val exception = assertThrows<NotFoundException> {
      cartService.create(createCartCommand)
    }
    assertThat(exception.message).isEqualTo("Product with id=$product2Id does not exist")
  }

  @Test
  fun `given create cart command with non-existing recipe when create then throws NotFoundException`() {
    // Given
    val product1Id = UUID.randomUUID()
    val product2Id = UUID.randomUUID()
    val recipeId = UUID.randomUUID()
    val createCartCommand = CreateCartCommand(
      products = listOf(product1Id, product2Id),
      recipes = listOf(recipeId)
    )
    given(productService.existsById(product1Id)).willReturn(true)
    given(productService.existsById(product2Id)).willReturn(true)
    given(recipeService.existsById(recipeId)).willReturn(false)

    // When - Then
    val exception = assertThrows<NotFoundException> {
      cartService.create(createCartCommand)
    }
    assertThat(exception.message).isEqualTo("Recipe with id=$recipeId does not exist")
  }

  @Test
  fun `given valid add product request when addProduct then does not throw exception`() {
    // Given
    val cartId = UUID.randomUUID()
    val productId = UUID.randomUUID()
    given(cartPersistencePort.getById(cartId)).willReturn(
      Cart(
        id = cartId,
        products = emptyList(),
        recipes = emptyList(),
        totalInCents = 0
      )
    )
    given(productService.existsById(productId)).willReturn(true)

    // When - then
    assertDoesNotThrow { cartService.addProduct(cartId, productId) }
  }

  @Test
  fun `given add product request with cartId does not exists when addProduct then throws NotFoundException`() {
    // Given
    val cartId = UUID.randomUUID()
    val productId = UUID.randomUUID()
    given(cartPersistencePort.getById(cartId)).willReturn(null)

    // When - then
    val exception = assertThrows<NotFoundException> {
      cartService.addProduct(cartId, productId)
    }
    assertThat(exception.message).isEqualTo("Cart with id=$cartId not found!")
  }

  @Test
  fun `given add product request with productId does not exists when addProduct then throws NotFoundException`() {
    // Given
    val cartId = UUID.randomUUID()
    val productId = UUID.randomUUID()
    given(cartPersistencePort.getById(cartId)).willReturn(
      Cart(
        id = cartId,
        products = emptyList(),
        recipes = emptyList(),
        totalInCents = 0
      )
    )
    given(productService.existsById(productId)).willReturn(false)

    // When - then
    val exception = assertThrows<NotFoundException> {
      cartService.addProduct(cartId, productId)
    }
    assertThat(exception.message).isEqualTo("Product with id=$productId not found!")
  }

  @Test
  fun `given valid add recipe request when addRecipe then does not throw exception`() {
    // Given
    val cartId = UUID.randomUUID()
    val recipeId = UUID.randomUUID()
    given(cartPersistencePort.getById(cartId)).willReturn(
      Cart(
        id = cartId,
        products = emptyList(),
        recipes = emptyList(),
        totalInCents = 0
      )
    )
    given(recipeService.existsById(recipeId)).willReturn(true)

    // When - then
    assertDoesNotThrow { cartService.addRecipe(cartId, recipeId) }
  }

  @Test
  fun `given add recipe request with cartId does not exists when addRecipe then throws NotFoundException`() {
    // Given
    val cartId = UUID.randomUUID()
    val recipeId = UUID.randomUUID()
    given(cartPersistencePort.getById(cartId)).willReturn(null)

    // When - then
    val exception = assertThrows<NotFoundException> {
      cartService.addRecipe(cartId, recipeId)
    }
    assertThat(exception.message).isEqualTo("Cart with id=$cartId not found!")
  }

  @Test
  fun `given add recipe request with recipeId does not exists when addRecipe then throws NotFoundException`() {
    // Given
    val cartId = UUID.randomUUID()
    val recipeId = UUID.randomUUID()
    given(cartPersistencePort.getById(cartId)).willReturn(
      Cart(
        id = cartId,
        products = emptyList(),
        recipes = emptyList(),
        totalInCents = 0
      )
    )
    given(recipeService.existsById(recipeId)).willReturn(false)

    // When - then
    val exception = assertThrows<NotFoundException> {
      cartService.addRecipe(cartId, recipeId)
    }
    assertThat(exception.message).isEqualTo("Recipe with id=$recipeId not found")
  }

  @Test
  fun `given valid delete recipe request when deleteRecipe then does not throw exception`() {
    // Given
    val cartId = UUID.randomUUID()
    val recipeId = UUID.randomUUID()
    val cart = Cart(
      id = cartId,
      products = emptyList(),
      recipes = listOf(Recipe(id = recipeId, name = "Test Recipe")),
      totalInCents = 0
    )
    given(cartPersistencePort.getById(cartId)).willReturn(cart)

    // When - then
    assertDoesNotThrow { cartService.deleteRecipe(cartId, recipeId) }
  }

  @Test
  fun `given delete recipe request with cartId does not exists when deleteRecipe then throws NotFoundException`() {
    // Given
    val cartId = UUID.randomUUID()
    val recipeId = UUID.randomUUID()
    given(cartPersistencePort.getById(cartId)).willReturn(null)

    // When - then
    val exception = assertThrows<NotFoundException> {
      cartService.deleteRecipe(cartId, recipeId)
    }
    assertThat(exception.message).isEqualTo("Cart with id=$cartId not found!")
  }

  @Test
  fun `given delete recipe request with recipeId does not exists in cart when deleteRecipe then throws NotFoundException`() {
    // Given
    val cartId = UUID.randomUUID()
    val recipeId = UUID.randomUUID()
    val cart = Cart(
      id = cartId,
      products = emptyList(),
      recipes = emptyList(),
      totalInCents = 0
    )
    given(cartPersistencePort.getById(cartId)).willReturn(cart)

    // When - then
    val exception = assertThrows<NotFoundException> {
      cartService.deleteRecipe(cartId, recipeId)
    }
    assertThat(exception.message).isEqualTo("Cart $cartId do not have such recipe with id $recipeId")
  }
}