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
@Table(name = "carts")
class CartEntity(
  @Id
  @Column(nullable = false)
  var id: UUID = UUID.randomUUID(),

  @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
  @JoinColumn(name = "cartId", referencedColumnName = "id")
  @Fetch(FetchMode.SUBSELECT)
  var products: MutableList<CartProductEntity> = mutableListOf(),

  @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
  @JoinColumn(name = "cartId", referencedColumnName = "id")
  @Fetch(FetchMode.SUBSELECT)
  var recipes: MutableList<CartRecipeEntity> = mutableListOf(),

  var totalInCents: Int = 0
)