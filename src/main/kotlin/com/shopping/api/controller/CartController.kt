package com.shopping.api.controller

import com.shopping.api.converter.CartConverter
import com.shopping.api.dto.CartDto
import com.shopping.api.dto.CreateCartCommandDto
import com.shopping.core.exception.NotFoundException
import com.shopping.core.service.CartService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller
@RequestMapping("api/v1/carts")
class CartController(private val cartService: CartService, private val converter: CartConverter) {

  @GetMapping("/{id}")
  fun getById(@PathVariable id: UUID): ResponseEntity<CartDto> {
    val cart = cartService.getById(id) ?: throw NotFoundException("Cart $id not found!")
    return ResponseEntity.ok(converter.toDto(cart))
  }

  @PostMapping
  fun create(@RequestBody createCartCommandDto: CreateCartCommandDto): ResponseEntity<UUID> {
    val command = converter.toCommand(createCartCommandDto)
    val createdId = cartService.create(command)
    return ResponseEntity.ok(createdId)
  }

  @PostMapping("/{id}/recipes/{recipeId}")
  fun addRecipe(@PathVariable id: UUID, @PathVariable recipeId: UUID): ResponseEntity<Void> {
    cartService.addRecipe(id, recipeId)
    return ResponseEntity.ok().build()
  }

  @DeleteMapping("/{cartId}/recipes/{recipeId}")
  fun deleteRecipe(@PathVariable cartId: UUID, @PathVariable recipeId: UUID): ResponseEntity<Void> {
    cartService.deleteRecipe(cartId, recipeId)
    return ResponseEntity.ok().build()
  }
}