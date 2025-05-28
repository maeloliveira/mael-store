package com.bookstore.mael.store.extension

import com.bookstore.mael.store.controller.request.PostBookRequest
import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.controller.request.PostCustomerRequest
import com.bookstore.mael.store.controller.request.PutBookRequest
import com.bookstore.mael.store.controller.request.PutCustomerRequest
import com.bookstore.mael.store.enum.BookStatus
import com.bookstore.mael.store.enum.CustomerStatus
import com.bookstore.mael.store.model.BookModel
import lombok.`var`

fun PostCustomerRequest.toCustomerModel() : CustomerModel {
    return CustomerModel(name = this.name, email = this.email, status = CustomerStatus.ATIVO)
}

fun PutCustomerRequest.toCustomerModel(previousValue: CustomerModel) : CustomerModel {
    return CustomerModel(id = previousValue.id, name = this.name, email = this.email, status = previousValue.status)
}

fun PostBookRequest.toBookModel(customer: CustomerModel): BookModel{
    return BookModel(
        name = this.name,
        price = this.price,
        status = BookStatus.ATIVO,
        customer = customer)
}

fun PutBookRequest.toBookModel(previousValue: BookModel): BookModel{
    return BookModel(
        id = previousValue.id,
        name = this.name ?: previousValue.name,
        price = this.price ?: previousValue.price,
        status = previousValue.status,
        customer = previousValue.customer
    )
}