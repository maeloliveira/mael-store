package com.bookstore.mael.store.controller.mapper

import com.bookstore.mael.store.enums.BookStatus
import com.bookstore.mael.store.enums.CustomerStatus
import com.bookstore.mael.store.exception.NotFoundException
import com.bookstore.mael.store.model.BookModel
import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.request.PostPurchaseRequest
import com.bookstore.mael.store.service.BookService
import com.bookstore.mael.store.service.CustomerService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PurchaseMapperTest {

    private val bookService = mockk<BookService>()
    private val customerService = mockk<CustomerService>()
    private val mapper = PurchaseMapper(bookService, customerService)
    private val customer = CustomerModel(
        id = 10,
        name = "Customer",
        email = "customer@example.com",
        status = CustomerStatus.ATIVO,
        password = "unused"
    )

    @Test
    fun `maps customer and books and calculates purchase total`() {
        val request = PostPurchaseRequest(customerId = 10, bookIds = setOf(21, 22))
        val books = listOf(
            book(id = 21, price = "12.50"),
            book(id = 22, price = "7.50")
        )
        every { customerService.findById(10) } returns customer
        every { bookService.findAllByIds(setOf(21, 22)) } returns books

        val purchase = mapper.toModel(request)

        assertEquals(customer, purchase.customer)
        assertEquals(books, purchase.books)
        assertEquals(BigDecimal("20.00"), purchase.price)
        verify(exactly = 1) { customerService.findById(10) }
        verify(exactly = 1) { bookService.findAllByIds(setOf(21, 22)) }
    }

    @Test
    fun `throws ML-101 when not all requested books are found`() {
        val request = PostPurchaseRequest(customerId = 10, bookIds = setOf(21, 22))
        every { customerService.findById(10) } returns customer
        every { bookService.findAllByIds(setOf(21, 22)) } returns listOf(book(id = 21, price = "12.50"))

        val exception = assertThrows(NotFoundException::class.java) {
            mapper.toModel(request)
        }

        assertEquals("ML-101", exception.errorCode)
        assertEquals("Book [[21, 22]] not exists", exception.message)
    }

    @Test
    fun `propagates the customer not found error`() {
        val request = PostPurchaseRequest(customerId = 10, bookIds = emptySet())
        every { customerService.findById(10) } throws NotFoundException("Customer [10] not exists", "ML-202")

        val exception = assertThrows(NotFoundException::class.java) {
            mapper.toModel(request)
        }

        assertEquals("ML-202", exception.errorCode)
        verify(exactly = 0) { bookService.findAllByIds(any()) }
    }

    @Test
    fun `allows an empty book set and calculates a zero total`() {
        val request = PostPurchaseRequest(customerId = 10, bookIds = emptySet())
        every { customerService.findById(10) } returns customer
        every { bookService.findAllByIds(emptySet()) } returns emptyList()

        val purchase = mapper.toModel(request)

        assertEquals(emptyList<BookModel>(), purchase.books)
        assertEquals(BigDecimal.ZERO, purchase.price)
    }

    private fun book(id: Int, price: String) = BookModel(
        id = id,
        name = "Book $id",
        price = BigDecimal(price),
        status = BookStatus.ATIVO
    )
}
