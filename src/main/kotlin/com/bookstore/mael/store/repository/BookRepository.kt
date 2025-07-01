package com.bookstore.mael.store.repository

import com.bookstore.mael.store.enum.BookStatus
import com.bookstore.mael.store.model.BookModel
import com.bookstore.mael.store.model.CustomerModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<BookModel, Int>{
    fun findByStatus(status: BookStatus, pageable: Pageable): Page<BookModel>
    fun findByCustomer(customer: CustomerModel): List<BookModel>
}