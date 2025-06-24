package com.shopping.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name="recipe_items")
class RecipeItemEntity (
  @Id
  @Column(nullable = false)
  var id: UUID = UUID.randomUUID(),

  @OneToOne
  @JoinColumn(name = "product_id", referencedColumnName = "id")
  var product: ProductEntity
)