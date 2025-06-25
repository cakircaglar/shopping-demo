package com.shopping.api.converter

import com.shopping.core.model.Product
import org.junit.jupiter.api.Test
import java.util.UUID

class ProductConverterTest {

  val converter  = ProductConverter()
  @Test
  fun demo(){
    val model = Product(UUID.randomUUID(), "test", 10)

    val result = converter.toDto(model)
  }
}