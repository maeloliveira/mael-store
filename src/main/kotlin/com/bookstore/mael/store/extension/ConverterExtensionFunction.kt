package com.bookstore.mael.store.extension

import com.bookstore.mael.store.controller.request.PostBookRequest
import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.controller.request.PostCustomerRequest
import com.bookstore.mael.store.controller.request.PutBookRequest
import com.bookstore.mael.store.controller.request.PutCustomerRequest
import com.bookstore.mael.store.controller.response.BookResponse
import com.bookstore.mael.store.controller.response.CustomerResponse
import com.bookstore.mael.store.enums.BookStatus
import com.bookstore.mael.store.enums.CustomerStatus
import com.bookstore.mael.store.model.BookModel

fun PostCustomerRequest.toCustomerModel(): CustomerModel {
    return CustomerModel(
        name = this.name,
        email = this.email,
        status = CustomerStatus.ATIVO,
        password = this.password
    )
}

fun PutCustomerRequest.toCustomerModel(previousValue: CustomerModel): CustomerModel {
    return CustomerModel(
        id = previousValue.id,
        name = this.name,
        email = this.email,
        status = previousValue.status,
        password = previousValue.password
    )
}

fun PostBookRequest.toBookModel(customer: CustomerModel): BookModel {
    return BookModel(
        name = this.name,
        price = this.price,
        status = BookStatus.ATIVO,
        customer = customer
    )
}

fun PutBookRequest.toBookModel(previousValue: BookModel): BookModel {
    return BookModel(
        id = previousValue.id,
        name = this.name ?: previousValue.name,
        price = this.price ?: previousValue.price,
        status = previousValue.status,
        customer = previousValue.customer
    )
}

fun CustomerModel.toResponse(): CustomerResponse {
    return CustomerResponse(
        id = this.id,
        name = this.name,
        email = this.email,
        status = this.status
    )
}

fun BookModel.toResponse(): BookResponse {
    return BookResponse(
        id = this.id,
        name = this.name,
        price = this.price,
        customer = this.customer,
        status = this.status
    )
}
