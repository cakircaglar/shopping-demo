package com.shopping.persistence.converter

import com.shopping.core.model.Cart
import com.shopping.core.model.Product
import com.shopping.core.model.Recipe
import com.shopping.persistence.entities.CartEntity
import com.shopping.persistence.entities.ProductEntity
import com.shopping.persistence.entities.RecipeEntity
import org.springframework.stereotype.Component

@Component
class EntityConverter {

  fun toProductModel(entity: ProductEntity): Product{
    return Product(
      id = entity.id,
      name = entity.name,
      priceInCents = entity.priceInCents
    )
  }

  fun toRecipeModel(entity: RecipeEntity): Recipe{
    return Recipe(
      id = entity.id,
      name = entity.name,
      products = entity.items.map {
        toProductModel(it.product)
      }
    )
  }

  fun toCartModel(entity: CartEntity): Cart {
    return Cart(
      id = entity.id,
      products = entity.products.map { toProductModel(it.product) },
      recipes = entity.recipes.map { toRecipeModel(it.recipe) },
      totalInCents = entity.totalInCents
    )
  }
}