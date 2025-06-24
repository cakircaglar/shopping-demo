package com.shopping.core.exception

open class DomainException(var errorCode: String, message: String?) : RuntimeException(message)
