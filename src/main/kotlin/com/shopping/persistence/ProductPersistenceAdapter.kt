package com.shopping.persistence

import com.shopping.core.model.Product
import com.shopping.core.model.command.CreateProductCommand
import com.shopping.core.persistence.ProductPersistencePort
import com.shopping.persistence.converter.EntityConverter
import com.shopping.persistence.entities.ProductEntity
import com.shopping.persistence.repositories.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class ProductPersistenceAdapter(
  private val productRepository: ProductRepository,
  private val entityConverter: EntityConverter,
) : ProductPersistencePort {
  override fun getAll(): List<Product> {
    val productEntities = productRepository.findAll()
    return productEntities.map { entityConverter.toProductModel(it) }
  }

  override fun create(createProductCommand: CreateProductCommand): UUID {
    val productEntity = toEntity(createProductCommand)
    return productRepository.save(productEntity).id
  }

  override fun getById(id: UUID): Product? {
    val productEntity = productRepository.findById(id)
    if (productEntity.isEmpty)
      return null

    return entityConverter.toProductModel(productEntity.get())
  }

  override fun existsById(id: UUID): Boolean {
    return productRepository.existsById(id)
  }

  private fun toEntity(createProductCommand: CreateProductCommand): ProductEntity {
    return ProductEntity(
      name = createProductCommand.name,
      priceInCents = createProductCommand.priceInCents
    )
  }
}