package com.bookstore.mael.store.controller.response

import com.bookstore.mael.store.enums.CustomerStatus

data class CustomerResponse(
    var id : Int? = null,
    var name : String,
    var email : String,
    var status: CustomerStatus
)