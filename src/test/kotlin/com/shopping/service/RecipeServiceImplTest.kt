package com.shopping.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.shopping.core.exception.NotFoundException
import com.shopping.core.model.Product
import com.shopping.core.model.Recipe
import com.shopping.core.model.command.CreateRecipeCommand
import com.shopping.core.persistence.RecipePersistencePort
import com.shopping.core.service.ProductService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class RecipeServiceImplTest {

  @Mock
  private lateinit var productService: ProductService

  @Mock
  private lateinit var recipePersistencePort: RecipePersistencePort
  private val recipeService by lazy {
    RecipeServiceImpl(
      recipePersistencePort,
      productService
    )
  }

  @Test
  fun `given valid recipe create command when create then returns created recipe's Id`() {
    // Given
    val product1Id = UUID.randomUUID()
    val product2Id = UUID.randomUUID()
    val createRecipeCommand = CreateRecipeCommand(
      name = "Recipe 1",
      products = listOf(product1Id, product2Id)
    )
    val expectedRecipeId = UUID.randomUUID()
    given(productService.existsById(product1Id)).willReturn(true)
    given(productService.existsById(product2Id)).willReturn(true)
    given(recipePersistencePort.create(createRecipeCommand)).willReturn(expectedRecipeId)

    // When
    val recipeId = recipeService.create(createRecipeCommand)

    // Then
    assertThat(recipeId).isEqualTo(expectedRecipeId)
  }

  @Test
  fun `given create recipe command which contains non-existing product when create then throws NotFoundException`() {
    // Given
    val product1Id = UUID.randomUUID()
    val product2Id = UUID.randomUUID()
    val createRecipeCommand = CreateRecipeCommand(
      name = "Recipe 1",
      products = listOf(product1Id, product2Id)
    )
    given(productService.existsById(product1Id)).willReturn(true)
    given(productService.existsById(product2Id)).willReturn(false)

    // When - Then
    val exception = assertThrows<NotFoundException> {
      recipeService.create(createRecipeCommand)
    }
    assertThat(exception.message).isEqualTo("Product with id=$product2Id not found")
  }

  @Test
  fun `given empty recipes in database when getAll is called then return empty list`() {
    // Given
    given(recipePersistencePort.getAll()).willReturn(emptyList())

    // When
    val recipes = recipeService.getAll()

    // Then
    assertThat(recipes).isEmpty()
  }

  @Test
  fun `given some recipes in database when getAll is called then returns recipes`() {
    // Given
    val product1 = Product(
      id = UUID.randomUUID(),
      name = "Product 1",
      priceInCents = 100,
    )
    val product2 = Product(
      id = UUID.randomUUID(),
      name = "Product 2",
      priceInCents = 200,
    )
    val recipe = Recipe(
      id = UUID.randomUUID(),
      name = "Recipe 1",
      products = listOf(product1, product2),
    )
    given(recipePersistencePort.getAll()).willReturn(listOf(recipe))

    // When
    val recipes = recipeService.getAll()

    // Then
    assertThat(recipes.size).isEqualTo(1)
    assertThat(recipes[0]).isEqualTo(recipe)
  }

  @Test
  fun `given existing recipe ID when existsById is called then returns true`() {
    // Given
    val recipeId = UUID.randomUUID()
    given(recipePersistencePort.existsById(recipeId)).willReturn(true)

    // When
    val exists = recipeService.existsById(recipeId)

    // Then
    assertThat(exists).isEqualTo(true)
  }

  @Test
  fun `given non-existing recipe ID when existsById is called then returns false`() {
    // Given
    val recipeId = UUID.randomUUID()
    given(recipePersistencePort.existsById(recipeId)).willReturn(false)

    // When
    val exists = recipeService.existsById(recipeId)

    // Then
    assertThat(exists).isEqualTo(false)
  }
}