package com.bookstore.mael.store.extension

import com.bookstore.mael.store.CustomerModel
import com.bookstore.mael.store.controller.request.PostCustomerRequest

fun PostCustomerRequest.toCustomerModel() : CustomerModel {
    return CustomerModel(name = this.name, email = this.email)
}

fun PostCustomerRequest.toCustomerModel(id: String) : CustomerModel {
    return CustomerModel(id = id, name = this.name, email = this.email)
}