package com.bookstore.mael.store.events

import com.bookstore.mael.store.service.BookService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class UpdateSoldBookListener(
    private val bookService: BookService
) {
    @Async
    @EventListener
    fun listen(purchaseEvent: PurchaseEvent){
        bookService.purchase(purchaseEvent.purchaseModel.books)
    }
}