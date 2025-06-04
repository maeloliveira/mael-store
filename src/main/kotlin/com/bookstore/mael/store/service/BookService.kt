package com.bookstore.mael.store.service

import com.bookstore.mael.store.enum.BookStatus
import com.bookstore.mael.store.exception.BookNotFoundException
import com.bookstore.mael.store.model.BookModel
import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.repository.BookRepository
import mu.KotlinLogging
import org.hibernate.internal.CoreLogging.logger
import org.springframework.stereotype.Service

@Service
class BookService(
    val bookRepository: BookRepository
) {

    private val logger = KotlinLogging.logger {}

    fun create(book: BookModel) {
        bookRepository.save(book)
        logger.info {"Book ${book.name}, with id= ${book.id} was created "}
    }

    fun findAll(): List<BookModel> {
        return bookRepository.findAll().toList()
    }

    fun findActives(): List<BookModel> {
        return bookRepository.findByStatus(BookStatus.ATIVO)
    }

    fun deleteBook(id: Int) {
        if (!bookRepository.existsById(id)) {
            throw BookNotFoundException(id)
        }
        bookRepository.deleteById(id)
        logger.info{"Book id= ${id} deleted"}
    }

    fun update(book: BookModel) {
        if (!bookRepository.existsById(book.id!!)) {
            throw BookNotFoundException(book.id!!)
        }
        bookRepository.save(book)
    }

    fun findById(id: Int): BookModel {
        return bookRepository.findById(id).orElseThrow()
    }

    fun deleteByCustomer(customer: CustomerModel) {
        val books = bookRepository.findByCustomer(customer)
        for (book in books) {
            book.status = BookStatus.DELETADO
        }
        bookRepository.saveAll(books)
        logger.info{"Book ${customer.name} was deleted "}
    }


}
