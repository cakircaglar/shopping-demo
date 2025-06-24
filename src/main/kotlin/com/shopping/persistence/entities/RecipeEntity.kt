package com.shopping.persistence.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.util.UUID

@Entity
@Table(name = "recipes")
class RecipeEntity(
  @Id
  @Column(nullable = false)
  var id: UUID = UUID.randomUUID(),

  @Column(nullable = false)
  var name: String,

  @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
  @JoinColumn(name = "recipeId", referencedColumnName = "id")
  @Fetch(FetchMode.SUBSELECT)
  var items: MutableList<RecipeItemEntity> = mutableListOf(),
)
{
  fun totalPrice():Int{
    return items.sumOf { it.product.priceInCents }
  }
}