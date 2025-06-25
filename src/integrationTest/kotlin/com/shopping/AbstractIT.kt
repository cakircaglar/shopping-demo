package com.shopping

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestExecutionListeners

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(initializers = [ReusablePostgreSQLInitializer::class])
@TestExecutionListeners(
  value = [GenericTestExecutionListener::class],
  mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
abstract class AbstractIT
