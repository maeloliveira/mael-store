package com.bookstore.mael.store.service

import com.bookstore.mael.store.enum.BookStatus
import com.bookstore.mael.store.enum.Errors
import com.bookstore.mael.store.exception.BookNotFoundException
import com.bookstore.mael.store.exception.NotFoundException
import com.bookstore.mael.store.model.BookModel
import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.repository.BookRepository
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

import org.springframework.stereotype.Service

@Service
class BookService(
    val bookRepository: BookRepository
) {

    private val logger = KotlinLogging.logger {}

    fun create(book: BookModel) {
        bookRepository.save(book)
        logger.info { "Book ${book.name}, with id= ${book.id} was created " }
    }

    fun findAll(pageable: Pageable): Page<BookModel> {
        return bookRepository.findAll(pageable)
    }

    fun findActives(pageable: Pageable): Page<BookModel> {
        return bookRepository.findByStatus(BookStatus.ATIVO, pageable)
    }

    fun deleteBook(id: Int) {
        if (!bookRepository.existsById(id)) {
            throw BookNotFoundException(id)
        }
        bookRepository.deleteById(id)
        logger.info { "Book id= ${id} deleted" }
    }

    fun update(book: BookModel) {
        if (!bookRepository.existsById(book.id!!)) {
            throw NotFoundException(Errors.ML101.message.format(book.id!!), Errors.ML101.code)
        }
        bookRepository.save(book)
    }

    fun findById(id: Int): BookModel {
        return bookRepository.findById(id).orElseThrow {
            NotFoundException(Errors.ML101.message.format(id), Errors.ML101.code)
        }
    }

    fun deleteByCustomer(customer: CustomerModel) {
        val books = bookRepository.findByCustomer(customer)
        for (book in books) {
            book.status = BookStatus.DELETADO
        }
        bookRepository.saveAll(books)
        logger.info { "Book ${customer.name} was deleted " }
    }

    fun findAllByIds(bookId: Set<Int>): List<BookModel> {
        return bookRepository.findAllById(bookId).toList()
    }

    fun purchase(books: MutableList<BookModel>) {
        books.map {
            it.status = BookStatus.VENDIDO
        }
        bookRepository.saveAll(books)
    }


}
