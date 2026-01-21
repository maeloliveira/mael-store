package com.bookstore.mael.store.service

import com.bookstore.mael.store.exception.BookNotFoundException
import com.bookstore.mael.store.exception.NotFoundException
import com.bookstore.mael.store.model.BookModel
import com.bookstore.mael.store.repository.BookRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BookServiceTest {

    private val bookRepository = mockk<BookRepository>()
    private val bookService = BookService(bookRepository)
    private val book = mockk<BookModel>(relaxed = true)

    @Test
    fun `should create book with success`() {

        every { bookRepository.save(book) } returns book

        bookService.create(book)

        verify(exactly = 1) { bookRepository.save(book) }

    }

    @Test
    fun `should be BookNotFoundException when id not exist`() {
        val id = 99

        every { bookRepository.existsById(id) } returns false

        assertThrows(BookNotFoundException::class.java) {
            bookService.deleteBook(id)
        }

        verify(exactly = 0) { bookRepository.deleteById(id) }
    }

    @Test
    fun `should throw NotFoundException when update and id not exist`() {

        val id = 43554

        every { book.id } returns id
        every { bookRepository.existsById(id) } returns false

        assertThrows(NotFoundException::class.java) {
            bookService.update(book)
        }

        verify(exactly = 0) {  bookRepository.save(any())}

    }

    @Test
    fun `should update book with success`() {
        val id = 431

        every { book.id } returns id
        every { bookRepository.existsById(id) } returns true
        every { bookRepository.save(book) } returns book

        bookService.update(book)

        verify(exactly = 0) {  bookRepository.save(book)}

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