package com.shopping.api.converter

import com.shopping.api.dto.CartDto
import com.shopping.api.dto.CreateCartCommandDto
import com.shopping.core.model.Cart
import com.shopping.core.model.command.CreateCartCommand
import org.springframework.stereotype.Component

@Component
class CartConverter(private val productConverter: ProductConverter, private val recipeConverter: RecipeConverter) {

  fun toDto(model: Cart): CartDto {
    return CartDto(
      id = model.id,
      products = model.products.map { productConverter.toDto(it) },
      recipes = model.recipes.map { recipeConverter.toDto(it) },
      totalInCents = model.totalInCents
    )
  }

  fun toCommand(dto: CreateCartCommandDto): CreateCartCommand {
    return CreateCartCommand(
      products = dto.products,
      recipes = dto.recipes
    )
  }
}