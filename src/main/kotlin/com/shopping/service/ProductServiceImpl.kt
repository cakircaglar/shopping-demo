package com.shopping.service

import com.shopping.core.model.Product
import com.shopping.core.model.command.CreateProductCommand
import com.shopping.core.persistence.ProductPersistencePort
import com.shopping.core.service.ProductService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProductServiceImpl(private val productPersistencePort: ProductPersistencePort) : ProductService {
  override fun getAll(): List<Product> {
    return productPersistencePort.getAll()
  }

  override fun create(createProductCommand: CreateProductCommand): UUID {
    return productPersistencePort.create(createProductCommand)
  }

  override fun getById(id: UUID): Product? {
    return productPersistencePort.getById(id)
  }

  override fun existsById(id: UUID): Boolean {
    return productPersistencePort.existsById(id)
  }
}