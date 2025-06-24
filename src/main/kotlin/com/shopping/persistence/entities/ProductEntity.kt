package com.shopping.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "products")
class ProductEntity (
  @Id
  @Column(nullable = false)
  var id: UUID = UUID.randomUUID(),

  @Column(nullable = false)
  var name: String,

  var priceInCents: Int
)


