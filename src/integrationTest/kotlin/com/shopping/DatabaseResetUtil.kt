package com.shopping


import org.hibernate.Session
import org.springframework.stereotype.Component
import jakarta.persistence.EntityManager
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.transaction.Transactional

@Component
class DatabaseResetUtil(private val entityManager: EntityManager) {

  @Transactional
  fun reset() {
    val session: Session = entityManager.unwrap(Session::class.java)
    val metamodel = session.sessionFactory.metamodel
    metamodel.entities
      .asSequence()
      .map { it.javaType }
      .filter { it.isAnnotationPresent(Table::class.java) }
      .onEach {
        entityManager.createNativeQuery("TRUNCATE TABLE ${it.getAnnotation(Table::class.java).name} CASCADE")
          .executeUpdate()
      }
      .flatMap { it.declaredFields.toMutableList() }
      .filter { it.isAnnotationPresent(SequenceGenerator::class.java) }
      .forEach {
        entityManager.createNativeQuery("ALTER SEQUENCE ${it.getAnnotation(SequenceGenerator::class.java).sequenceName} RESTART WITH 1")
          .executeUpdate()
      }
  }
}
