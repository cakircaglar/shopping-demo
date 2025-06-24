package com.shopping.persistence.repositories

import com.shopping.persistence.entities.CartEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CartRepository : JpaRepository<CartEntity, UUID>