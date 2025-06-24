package com.shopping.persistence

import com.shopping.core.model.Recipe
import com.shopping.core.model.command.CreateRecipeCommand
import com.shopping.core.persistence.RecipePersistencePort
import com.shopping.persistence.converter.EntityConverter
import com.shopping.persistence.entities.RecipeEntity
import com.shopping.persistence.entities.RecipeItemEntity
import com.shopping.persistence.repositories.ProductRepository
import com.shopping.persistence.repositories.RecipeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class RecipePersistenceAdapter(
  private val recipeRepository: RecipeRepository,
  private val productRepository: ProductRepository,
  private val entityConverter: EntityConverter,
) : RecipePersistencePort {
  override fun create(createRecipeCommand: CreateRecipeCommand): UUID {
    val recipeEntity = toEntity(createRecipeCommand)
    return recipeRepository.save(recipeEntity).id
  }

  override fun getAll(): List<Recipe> {
    val recipes = recipeRepository.findAll()
    return recipes.map { entityConverter.toRecipeModel(it) }
  }

  override fun existsById(id: UUID): Boolean {
    return recipeRepository.existsById(id)
  }

  private fun toEntity(createRecipeCommand: CreateRecipeCommand): RecipeEntity {
    return RecipeEntity(
      name = createRecipeCommand.name,
      items = createRecipeCommand.products.map {
        RecipeItemEntity(
          product = productRepository.findById(it).get() //no need to check, already done in service layer
        )
      }.toMutableList()
    )
  }

}