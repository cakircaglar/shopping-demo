package com.shopping.api.converter

import com.shopping.api.dto.CreateRecipeCommandDto
import com.shopping.api.dto.RecipeDto
import com.shopping.core.model.Recipe
import com.shopping.core.model.command.CreateRecipeCommand
import org.springframework.stereotype.Component

@Component
class RecipeConverter(private val productConverter: ProductConverter) {
  fun toCommand(dto: CreateRecipeCommandDto): CreateRecipeCommand {
    return CreateRecipeCommand(
      name = dto.name,
      products = dto.products
    )
  }

  fun toDto(model: Recipe): RecipeDto {
    return RecipeDto(
      id = model.id,
      name = model.name,
      products = model.products.map { productConverter.toDto(it) }
    )
  }


}