package com.bookstore.mael.store.controller.mapper

import com.bookstore.mael.store.enum.Errors
import com.bookstore.mael.store.exception.NotFoundException
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
        val customer = customerService.findById(request.customerId)
        val books = bookService.findAllByIds(request.bookIds.toSet())

        if(books.size != request.bookIds.size){
            throw NotFoundException(Errors.ML101.message.format(request.bookIds), Errors.ML101.code)
        }

        return PurchaseModel(
            customer = customer,
            books = books.toMutableList(),
            price = books.sumOf { it.price }
        )
    }
}