package com.bookstore.mael.store.controller.request

import com.bookstore.mael.store.controller.response.FieldErrorResponse

data class ErrorResponse (
    var httpCode: Int,
    var message: String,
    var internalCode: String,
    var errors: List<FieldErrorResponse>?
)