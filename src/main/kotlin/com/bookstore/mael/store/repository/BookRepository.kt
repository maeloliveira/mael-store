package com.bookstore.mael.store.repository

import com.bookstore.mael.store.enum.BookStatus
import com.bookstore.mael.store.model.BookModel
import com.bookstore.mael.store.model.CustomerModel
import org.springframework.data.repository.CrudRepository

interface BookRepository : CrudRepository<BookModel, Int>{
    fun findByStatus(status: BookStatus): List<BookModel>
    fun findByCustomer(customer: CustomerModel): List<BookModel>

}