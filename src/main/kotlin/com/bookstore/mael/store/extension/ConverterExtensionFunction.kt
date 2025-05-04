package com.bookstore.mael.store.extension

import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.controller.request.PostCustomerRequest
import com.bookstore.mael.store.controller.request.PutCustomerRequest

fun PostCustomerRequest.toCustomerModel() : CustomerModel {
    return CustomerModel(name = this.name, email = this.email)
}

fun PutCustomerRequest.toCustomerModel(id: Int) : CustomerModel {
    return CustomerModel(id = id, name = this.name, email = this.email)
}