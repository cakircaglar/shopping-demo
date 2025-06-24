package com.shopping.core.persistence

import com.shopping.core.model.Product
import com.shopping.core.model.command.CreateProductCommand
import java.util.UUID

interface ProductPersistencePort {
  fun getAll(): List<Product>
  fun create(createProductCommand: CreateProductCommand) : UUID
  fun getById(id: UUID) : Product?
  fun existsById(id: UUID) : Boolean
}