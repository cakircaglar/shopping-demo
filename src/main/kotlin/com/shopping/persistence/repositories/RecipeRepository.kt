package com.shopping.persistence.repositories

import com.shopping.persistence.entities.RecipeEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RecipeRepository : JpaRepository<RecipeEntity, UUID>