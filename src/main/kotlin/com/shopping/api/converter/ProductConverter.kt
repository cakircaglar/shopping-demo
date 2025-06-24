package com.shopping.api.converter

import com.shopping.api.dto.CreateProductCommandDto
import com.shopping.api.dto.ProductDto
import com.shopping.core.model.Product
import com.shopping.core.model.command.CreateProductCommand
import org.springframework.stereotype.Component

@Component
class ProductConverter {
  fun toCommand(dto: CreateProductCommandDto): CreateProductCommand {
    return CreateProductCommand(
      name = dto.name,
      priceInCents = dto.priceInCents
    )
  }

  fun toDto(model: Product): ProductDto {
    return ProductDto(
      id = model.id,
      name = model.name,
      priceInCents = model.priceInCents
    )
  }
}