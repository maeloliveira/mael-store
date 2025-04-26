package com.bookstore.mael.store.extension

import com.bookstore.mael.store.CustomerModel
import com.bookstore.mael.store.controller.request.PostCustomerRequest
import com.bookstore.mael.store.controller.request.PutCustomerRequest

fun PostCustomerRequest.toCustomerModel() : CustomerModel {
    return CustomerModel(name = this.name, email = this.email)
}

fun PutCustomerRequest.toCustomerModel(id: String) : CustomerModel {
    return CustomerModel(id = id, name = this.name, email = this.email)
}