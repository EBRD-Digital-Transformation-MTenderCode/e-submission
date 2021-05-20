package com.procurement.submission.infrastructure.exception

class EmptyStringException(val path: String) : RuntimeException(path)