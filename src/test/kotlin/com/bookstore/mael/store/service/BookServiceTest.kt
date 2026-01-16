package com.bookstore.mael.store.service

import com.bookstore.mael.store.exception.BookNotFoundException
import com.bookstore.mael.store.model.BookModel
import com.bookstore.mael.store.repository.BookRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class BookServiceTest {

    private val bookRepository = mockk<BookRepository>()
    private val bookService = BookService(bookRepository)
    private val book = mockk<BookModel>(relaxed = true)

    @Test
    fun `should creat book with sucess`() {

        every { bookRepository.save(book) } returns book

        bookService.create(book)

        verify(exactly = 1) { bookRepository.save(book) }

    }

    @Test
    fun `deve lancar BookNotFoundException quando id nao existir`() {
        val id = 99

        every { bookRepository.existsById(id) } returns false

        assertThrows(BookNotFoundException::class.java) {
            bookService.deleteBook(id)
        }

        verify(exactly = 0) { bookRepository.deleteById(id) }
    }

    @Test
    fun update() {
    }

    @Test
    fun findAll() {
    }

    @Test
    fun findActives() {
    }

    @Test
    fun findNotActives() {
    }



    @Test
    fun findById() {
    }

    @Test
    fun deleteByCustomer() {
    }

    @Test
    fun findAllByIds() {
    }

    @Test
    fun purchase() {
    }

}