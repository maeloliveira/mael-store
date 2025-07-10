package com.bookstore.mael.store.controller.request

import com.bookstore.mael.store.enum.CustomerStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class PutCustomerRequest (

    @field: NotEmpty(message = "Field name can't be empty")
    var name : String,
    @field: Email(message = "Please insert valid value at e-mail")
    var email : String,
    var status: CustomerStatus
)