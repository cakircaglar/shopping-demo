package com.shopping.persistence

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.shopping.core.model.Product
import com.shopping.core.model.Recipe
import com.shopping.core.model.command.CreateRecipeCommand
import com.shopping.persistence.converter.EntityConverter
import com.shopping.persistence.entities.ProductEntity
import com.shopping.persistence.entities.RecipeEntity
import com.shopping.persistence.entities.RecipeItemEntity
import com.shopping.persistence.repositories.ProductRepository
import com.shopping.persistence.repositories.RecipeRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class RecipePersistenceAdapterTest {
  @Mock
  private lateinit var recipeRepository: RecipeRepository

  @Mock
  private lateinit var productRepository: ProductRepository

  @Mock
  private lateinit var entityConverter: EntityConverter

  @Captor
  private lateinit var recipeEntityCaptor: ArgumentCaptor<RecipeEntity>

  private val recipePersistenceAdapter by lazy {
    RecipePersistenceAdapter(recipeRepository, productRepository, entityConverter)
  }

  @Test
  fun `given recipes in database when getAll then returns all recipes`() {
    //given
    val recipeEntity = RecipeEntity(
      id = UUID.randomUUID(),
      name = "Test Recipe 1",
      items = mutableListOf(
        RecipeItemEntity(
          product = ProductEntity(
            id = UUID.randomUUID(),
            name = "Test Product 1",
            priceInCents = 1000
          )
        )
      )
    )

    given(recipeRepository.findAll()).willReturn(listOf(recipeEntity))
    given(entityConverter.toRecipeModel(recipeEntity)).willReturn(
      Recipe(
        id = recipeEntity.id,
        name = recipeEntity.name,
        products = listOf(
          Product(
            id = recipeEntity.items[0].product.id,
            name = recipeEntity.items[0].product.name,
            priceInCents = recipeEntity.items[0].product.priceInCents
          )
        )
      )
    )


    //when
    val recipes = recipePersistenceAdapter.getAll()

    //then
    assertThat(recipes).isNotNull()
    assertThat(recipes.size).isEqualTo(1)
    assertThat(recipes[0].name).isEqualTo("Test Recipe 1")
    assertThat(recipes[0].products.size).isEqualTo(1)
    assertThat(recipes[0].products[0].name).isEqualTo("Test Product 1")
  }

  @Test
  fun `given valid create recipe command when create then returns created recipe's Id`() {
    // Given
    val productId = UUID.randomUUID()
    val createRecipeCommand = CreateRecipeCommand(
      name = "Test Recipe",
      products = listOf(productId)
    )

    val recipeEntity = RecipeEntity(
      id = UUID.randomUUID(),
      name = createRecipeCommand.name,
      items = mutableListOf(
        RecipeItemEntity(
          product = ProductEntity(id = productId, name = "Test Product", priceInCents = 1000)
        )
      )
    )

    given(productRepository.findById(productId)).willReturn(java.util.Optional.of(recipeEntity.items[0].product))
    given(recipeRepository.save(recipeEntityCaptor.capture())).willReturn(recipeEntity)

    // When
    val recipeId = recipePersistenceAdapter.create(createRecipeCommand)

    // Then
    assertThat(recipeId).isEqualTo(recipeEntity.id)
    recipeEntityCaptor.value.let {
      assertThat(it.name).isEqualTo(createRecipeCommand.name)
      assertThat(it.items.size).isEqualTo(1)
      assertThat(it.items[0].product.id).isEqualTo(productId)
    }
  }

  @Test
  fun `given existing recipe id when existsById then returns true`() {
    // Given
    val recipeId = UUID.randomUUID()
    given(recipeRepository.existsById(recipeId)).willReturn(true)

    // When
    val exists = recipePersistenceAdapter.existsById(recipeId)

    // Then
    assertThat(exists).isEqualTo(true)
  }

}