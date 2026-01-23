package com.bookstore.mael.store.service

import com.bookstore.mael.store.enums.BookStatus
import com.bookstore.mael.store.exception.BookNotFoundException
import com.bookstore.mael.store.exception.NotFoundException
import com.bookstore.mael.store.model.BookModel
import com.bookstore.mael.store.repository.BookRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
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
    fun `should delegate findAll to repository and return page`() {
        val pageable = mockk<Pageable>()
        val page = PageImpl(listOf(book))

        every { bookRepository.findAll(pageable) } returns page

        val result = bookService.findAll(pageable)

        assertEquals(page, result)

        verify (exactly = 1){ bookRepository.findAll(pageable) }
    }

    @Test
    fun `should find by actives books`() {
        val pageable = mockk<Pageable>()
        val page = PageImpl(listOf(book))
        val bookStatus = "ATIVO"

        every { bookRepository.findByStatus(BookStatus.ATIVO, pageable) } returns page

        val result = bookService.findActives(pageable)

        assertEquals(page, result)

        verify (exactly = 1){ bookRepository.findByStatus(BookStatus.ATIVO, pageable) }
    }


    @Test
    fun `should return empty page when no active books`() {
        val pageable = mockk<Pageable>()
        val emptyPage = PageImpl(emptyList<BookModel>())

        every { bookRepository.findByStatus(BookStatus.ATIVO, pageable) } returns emptyPage

        val result = bookService.findActives(pageable)

        assertEquals(0, result.content.size)
        verify(exactly = 1) { bookRepository.findByStatus(BookStatus.ATIVO, pageable) }
    }



    @Test
    fun `should success delete book by id`(){
        val id = 1

        every { bookRepository.existsById(id) } returns true
        every { bookRepository.deleteById(id) } returns Unit

        bookService.deleteBook(id)

        verify(exactly = 1) { bookRepository.deleteById(id) }
    }

    fun `should success delete book by costumer`(){
        val id = 1

        every { bookRepository.findByCustomer(id) } returns true
        every { bookRepository.findByCustomer(id) } returns Unit

        bookService.deleteBook(id)

        verify(exactly = 1) { bookRepository.deleteById(id) }
    }
    @Test
    fun findAllByIds() {
    }

    @Test
    fun purchase() {
    }

}