package com.shopping.core.exception

class NotFoundException(msg: String) : DomainException(CoreErrorType.NOT_FOUND.value, msg)
