package com.shopping

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer

class ReusablePostgreSQLInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
  companion object {
    val postgres = PostgreSQLContainer("postgres:15.4")

    init {
      postgres.start()
    }
  }

  override fun initialize(applicationContext: ConfigurableApplicationContext) {
    TestPropertyValues.of(
      "spring.datasource.url=${postgres.jdbcUrl}",
      "spring.datasource.username=${postgres.username}",
      "spring.datasource.password=${postgres.password}"
    ).applyTo(applicationContext)
  }
}
