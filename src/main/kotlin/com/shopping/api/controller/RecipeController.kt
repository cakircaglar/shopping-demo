package com.shopping.api.controller

import com.shopping.api.converter.RecipeConverter
import com.shopping.api.dto.CreateRecipeCommandDto
import com.shopping.api.dto.RecipeDto
import com.shopping.core.service.RecipeService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller
@RequestMapping("api/v1/recipes")
class RecipeController(private val recipeService: RecipeService, private val converter: RecipeConverter) {

  @GetMapping
  fun getAll(): ResponseEntity<List<RecipeDto>> {
    val recipes = recipeService.getAll()
    return ResponseEntity.ok(recipes.map { converter.toDto(it) })
  }

  @PostMapping
  fun create(@RequestBody createRecipeCommandDto: CreateRecipeCommandDto): ResponseEntity<UUID> {
    val command = converter.toCommand(createRecipeCommandDto)
    val createdId = recipeService.create(command)
    return ResponseEntity.ok(createdId)
  }
}