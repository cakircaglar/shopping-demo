package com.shopping.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "cart_recipes")
class CartRecipeEntity (
  @Id
  @Column(nullable = false)
  var id: UUID = UUID.randomUUID(),

  @OneToOne
  @JoinColumn(name = "recipe_id", referencedColumnName = "id")
  var recipe: RecipeEntity
)