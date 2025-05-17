package com.bookstore.mael.store.controller.request

import com.bookstore.mael.store.enum.CustomerStatus

data class PutCustomerRequest (
    var name : String,
    var email : String,
    var status: CustomerStatus
)