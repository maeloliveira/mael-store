package com.bookstore.mael.store.controller.request

import com.bookstore.mael.store.validation.EmailAvailable
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty


data class PostCustomerRequest (

    @field: NotEmpty(message = "Field name can't be empty")
    var name : String,
    @field: Email(message = "Please insert valid value at e-mail")
    @EmailAvailable(message = "Please verify your email because it's only use, ")
    var email : String

)