package com.shopping


import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

class GenericTestExecutionListener : TestExecutionListener {
  override fun afterTestMethod(testContext: TestContext) {
    clearDatabase(testContext)
  }

  private fun clearDatabase(testContext: TestContext) {
    val databaseResetUtil: DatabaseResetUtil = testContext.applicationContext.getBean(DatabaseResetUtil::class.java)
    databaseResetUtil.reset()
  }
}
