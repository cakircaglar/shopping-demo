package com.shopping.api.controller

import com.shopping.api.converter.ProductConverter
import com.shopping.api.dto.CreateProductCommandDto
import com.shopping.api.dto.ProductDto
import com.shopping.core.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller
@RequestMapping("api/v1/products")
class ProductController(private val productService: ProductService, private val converter: ProductConverter) {

  @GetMapping
  fun getAll(): ResponseEntity<List<ProductDto>> {
    val products = productService.getAll()
    return ResponseEntity.ok(products.map { converter.toDto(it) })
  }
  @PostMapping
  fun create(@RequestBody createProductCommandDto: CreateProductCommandDto): ResponseEntity<UUID> {
    val createdId = productService.create(converter.toCommand(createProductCommandDto))
    return ResponseEntity.ok(createdId)
  }
}