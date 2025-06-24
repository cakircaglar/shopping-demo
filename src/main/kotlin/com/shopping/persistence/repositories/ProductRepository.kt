package com.shopping.persistence.repositories

import com.shopping.persistence.entities.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProductRepository : JpaRepository<ProductEntity, UUID>