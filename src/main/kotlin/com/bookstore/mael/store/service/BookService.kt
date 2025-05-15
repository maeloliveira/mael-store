package com.bookstore.mael.store.service

import com.bookstore.mael.store.enum.BookStatus
import com.bookstore.mael.store.exception.BookNotFoundException
import com.bookstore.mael.store.model.BookModel
import com.bookstore.mael.store.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService (
    val bookRepository: BookRepository
){

    fun create(book: BookModel) {
        bookRepository.save(book)
    }

    fun findAll(): List<BookModel> {
        return bookRepository.findAll().toList()
    }

    fun findActives(): List<BookModel> {
        return bookRepository.findByStatus(BookStatus.ATIVO)
    }

    fun deleteBook(id: Int){
        if(!bookRepository.existsById(id)){
            throw BookNotFoundException(id)
        }
        bookRepository.deleteById(id)
    }

    fun update (book: BookModel){
        if(!bookRepository.existsById(book.id!!)){
            throw BookNotFoundException(book.id!!)
        }
        bookRepository.save(book)
    }



}
