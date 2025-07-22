package com.bookstore.mael.store.controller.mapper

import com.bookstore.mael.store.model.PurchaseModel
import com.bookstore.mael.store.request.PostPurchaseRequest
import com.bookstore.mael.store.service.BookService
import com.bookstore.mael.store.service.CustomerService
import org.springframework.stereotype.Component

@Component
class PurchaseMapper(
    private val bookService: BookService,
    private val customerService: CustomerService
) {
    fun toModel(request: PostPurchaseRequest): PurchaseModel {
        val customer = customerService.findById(request.costumerId)
        val books = bookService.findAllByIds(request.bookIds)

        return PurchaseModel(
            customer = customer,
            books = books,
            price = books.sumOf { it.price }
        )
    }
}