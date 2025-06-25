package com.shopping.persistence

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.shopping.core.model.Cart
import com.shopping.core.model.Product
import com.shopping.core.model.Recipe
import com.shopping.core.model.command.CreateCartCommand
import com.shopping.persistence.converter.EntityConverter
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
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class CartPersistenceAdapterTest {
  @Mock
  private lateinit var cartRepository: CartRepository

  @Mock
  private lateinit var productRepository: ProductRepository

  @Mock
  private lateinit var recipeRepository: RecipeRepository

  @Mock
  private lateinit var entityConverter: EntityConverter

  @Captor
  private lateinit var cartEntityCaptor: ArgumentCaptor<CartEntity>

  private val cartPersistenceAdapter by lazy {
    CartPersistenceAdapter(cartRepository, productRepository, recipeRepository, entityConverter)
  }

  @Test
  fun `given carts in database when getById then returns cart`() {
    //given
    val cartEntity = givenCartEntity()

    given(cartRepository.findById(cartEntity.id)).willReturn(java.util.Optional.of(cartEntity))
    given(entityConverter.toCartModel(cartEntity)).willReturn(
      Cart(
        id = cartEntity.id,
        products = listOf(
          Product(
            id = cartEntity.products.first().product.id,
            name = cartEntity.products.first().product.name,
            priceInCents = cartEntity.products.first().product.priceInCents
          )
        ),
        recipes = listOf(
          Recipe(
            id = cartEntity.recipes.first().recipe.id,
            name = cartEntity.recipes.first().recipe.name,
            products = cartEntity.recipes.first().recipe.items.map {
              Product(
                id = it.product.id,
                name = it.product.name,
                priceInCents = it.product.priceInCents
              )
            }
          )
        ),
        totalInCents = 3000
      )
    )

    //when
    val cart = cartPersistenceAdapter.getById(cartEntity.id)

    //then
    assertThat(cart).isNotNull()
    assertThat(cart!!.id).isEqualTo(cartEntity.id)
  }

  @Test
  fun `given valid cart create command when create then returns created cart id`() {
    //given
    val createCartCommand = CreateCartCommand(
      products = listOf(UUID.randomUUID()),
      recipes = listOf(UUID.randomUUID())
    )

    val productEntity = ProductEntity(
      id = createCartCommand.products.first(),
      name = "Test Product",
      priceInCents = 1000
    )

    val recipeEntity = RecipeEntity(
      id = createCartCommand.recipes.first(),
      name = "Test Recipe",
      items = mutableListOf(
        RecipeItemEntity(
          product = ProductEntity(
            id = UUID.randomUUID(),
            name = "Test Product in Recipe",
            priceInCents = 2000
          )
        )
      )
    )

    given(productRepository.findById(createCartCommand.products.first())).willReturn(java.util.Optional.of(productEntity))
    given(recipeRepository.findById(createCartCommand.recipes.first())).willReturn(java.util.Optional.of(recipeEntity))


    given(cartRepository.save(cartEntityCaptor.capture())).willAnswer { it.getArgument(0) }

    //when
    val createdCartId = cartPersistenceAdapter.create(createCartCommand)

    //then
    assertThat(createdCartId).isNotNull()
    cartEntityCaptor.value.let {
      assertThat(it.products.size).isEqualTo(1)
      assertThat(it.recipes.size).isEqualTo(1)
      assertThat(it.totalInCents).isEqualTo(productEntity.priceInCents + recipeEntity.totalPrice())
    }
  }

  @Test
  fun `given existing cart and product when addProduct then adds product to cart`() {
    //given
    val cartId = UUID.randomUUID()
    val productId = UUID.randomUUID()
    val productEntity = ProductEntity(
      id = productId,
      name = "Test Product",
      priceInCents = 1000
    )
    val cartEntity = givenCartEntity()
    val totalCartPriceBefore = cartEntity.totalInCents

    given(cartRepository.findById(cartId)).willReturn(java.util.Optional.of(cartEntity))
    given(productRepository.findById(productId)).willReturn(java.util.Optional.of(productEntity))
    given(cartRepository.save(cartEntityCaptor.capture())).willAnswer { it.getArgument(0) }

    //when
    cartPersistenceAdapter.addProduct(cartId, productId)

    //then
    assertThat(cartEntityCaptor.value.products.size).isEqualTo(2)
    assertThat(cartEntityCaptor.value.totalInCents).isEqualTo(totalCartPriceBefore + productEntity.priceInCents)
    assertThat(cartEntityCaptor.value.products.last().product.id).isEqualTo(productId)
  }

  @Test
  fun `given existing cart when addRecipe then adds recipe to cart`() {
    //given
    val cartId = UUID.randomUUID()
    val recipeId = UUID.randomUUID()
    val recipeEntity = RecipeEntity(
      id = recipeId,
      name = "Test Recipe",
      items = mutableListOf(
        RecipeItemEntity(
          product = ProductEntity(
            id = UUID.randomUUID(),
            name = "Test Product in Recipe",
            priceInCents = 2000
          )
        )
      )
    )
    val cartEntity = givenCartEntity()
    val totalCartPriceBefore = cartEntity.totalInCents

    given(cartRepository.findById(cartId)).willReturn(java.util.Optional.of(cartEntity))
    given(recipeRepository.findById(recipeId)).willReturn(java.util.Optional.of(recipeEntity))
    given(cartRepository.save(cartEntityCaptor.capture())).willAnswer { it.getArgument(0) }

    //when
    cartPersistenceAdapter.addRecipe(cartId, recipeId)

    //then
    assertThat(cartEntityCaptor.value.recipes.size).isEqualTo(2)
    assertThat(cartEntityCaptor.value.totalInCents).isEqualTo(totalCartPriceBefore + recipeEntity.totalPrice())
    assertThat(cartEntityCaptor.value.recipes.last().recipe.id).isEqualTo(recipeId)
  }

  //delete recipe
  @Test
  fun `given existing cart and recipe when deleteRecipe then removes recipe from cart`() {
    //given
    val cartEntity = givenCartEntity()
    val recipeToRemove = cartEntity.recipes.first().recipe
    val totalCartPriceBefore = cartEntity.totalInCents
    val recipeToRemoveTotalPrice = recipeToRemove.totalPrice()

    given(cartRepository.findById(cartEntity.id)).willReturn(java.util.Optional.of(cartEntity))
    given(cartRepository.save(cartEntityCaptor.capture())).willAnswer { it.getArgument(0) }

    //when
    cartPersistenceAdapter.deleteRecipe(cartEntity.id, recipeToRemove.id)

    //then
    assertThat(cartEntityCaptor.value.recipes.size).isEqualTo(0)
    assertThat(cartEntityCaptor.value.totalInCents).isEqualTo(totalCartPriceBefore - recipeToRemoveTotalPrice)
  }

  private fun givenCartEntity(): CartEntity {
    return CartEntity(
      id = UUID.randomUUID(),
      products = mutableListOf(
        CartProductEntity(
          id = UUID.randomUUID(),
          product = ProductEntity(
            id = UUID.randomUUID(),
            name = "Test Product",
            priceInCents = 1000
          )
        )
      ),
      recipes = mutableListOf(
        CartRecipeEntity(
          id = UUID.randomUUID(),
          recipe = RecipeEntity(
            id = UUID.randomUUID(),
            name = "Test Recipe",
            items = mutableListOf(
              RecipeItemEntity(
                product = ProductEntity(
                  id = UUID.randomUUID(),
                  name = "Test Product in Recipe",
                  priceInCents = 2000
                )
              )
            )
          )
        )
      ),
      totalInCents = 3000
    )
  }

}