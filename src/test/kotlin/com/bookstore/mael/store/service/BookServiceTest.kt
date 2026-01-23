package com.bookstore.mael.store.service

import com.bookstore.mael.store.enums.BookStatus
import com.bookstore.mael.store.exception.BookNotFoundException
import com.bookstore.mael.store.exception.NotFoundException
import com.bookstore.mael.store.model.BookModel
import com.bookstore.mael.store.model.CustomerModel
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

        verify(exactly = 0) { bookRepository.save(any()) }

    }

    @Test
    fun `should update book with success`() {
        val id = 431

        every { book.id } returns id
        every { bookRepository.existsById(id) } returns true
        every { bookRepository.save(book) } returns book

        bookService.update(book)

        verify(exactly = 1) { bookRepository.save(book) }

    }

    @Test
    fun `should delegate findAll to repository and return page`() {
        val pageable = mockk<Pageable>()
        val page = PageImpl(listOf(book))

        every { bookRepository.findAll(pageable) } returns page

        val result = bookService.findAll(pageable)

        assertEquals(page, result)

        verify(exactly = 1) { bookRepository.findAll(pageable) }
    }

    @Test
    fun `should find by actives books`() {
        val pageable = mockk<Pageable>()
        val page = PageImpl(listOf(book))

        every { bookRepository.findByStatus(BookStatus.ATIVO, pageable) } returns page

        val result = bookService.findActives(pageable)

        assertEquals(page, result)

        verify(exactly = 1) { bookRepository.findByStatus(BookStatus.ATIVO, pageable) }
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
    fun `should success delete book by id`() {
        val id = 1

        every { bookRepository.existsById(id) } returns true
        every { bookRepository.deleteById(id) } returns Unit

        bookService.deleteBook(id)

        verify(exactly = 1) { bookRepository.deleteById(id) }
    }

    @Test
    fun `should success delete book by costumer`() {
        val customer = mockk<CustomerModel>(relaxed = true)
        val book1 = mockk<BookModel>(relaxed = true)
        val book2 = mockk<BookModel>(relaxed = true)
        val books = listOf(book1, book2)

        every { bookRepository.findByCustomer(customer) } returns books
        every { bookRepository.saveAll(books) } returns books

        bookService.deleteByCustomer(customer)

        verify(exactly = 1) { bookRepository.findByCustomer(customer) }
        verify { book1.status = BookStatus.DELETADO }
        verify { book2.status = BookStatus.DELETADO }
        verify(exactly = 1) { bookRepository.saveAll(books) }
    }

    @Test
    fun `should find all book by Ids`() {
        val ids = setOf(1, 2)
        val iterable = listOf(book)

        every { bookRepository.findAllById(ids) } returns iterable

        val result = bookService.findAllByIds(ids)

        assertEquals(iterable, result)
        verify(exactly = 1) { bookRepository.findAllById(ids) }
    }


    @Test
    fun `should return empty list when repository returns empty iterable`() {
        val ids = setOf(3)

        every { bookRepository.findAllById(ids) } returns emptyList()

        val result = bookService.findAllByIds(ids)

        assertEquals(0, result.size)
        verify(exactly = 1) { bookRepository.findAllById(ids) }
    }


    @Test
    fun `should mark all books as VENDIDO and call saveAll`() {
        val book1 = mockk<BookModel>(relaxed = true)
        val book2 = mockk<BookModel>(relaxed = true)
        val books = mutableListOf(book1, book2)

        every { bookRepository.saveAll(books) } returns books

        bookService.purchase(books)

        verify(exactly = 1) { book1.status = BookStatus.VENDIDO }
        verify(exactly = 1) { book2.status = BookStatus.VENDIDO }
        verify(exactly = 1) { bookRepository.saveAll(books) }
    }

    @Test
    fun `should call saveAll with empty list when no books`() {
        val emptyBooks = mutableListOf<BookModel>()

        every { bookRepository.saveAll(emptyBooks) } returns emptyBooks

        bookService.purchase(emptyBooks)

        verify(exactly = 1) { bookRepository.saveAll(emptyBooks) }
    }
}