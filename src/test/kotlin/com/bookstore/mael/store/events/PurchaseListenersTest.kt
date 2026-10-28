package com.bookstore.mael.store.events

import com.bookstore.mael.store.enums.CustomerStatus
import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.model.PurchaseModel
import com.bookstore.mael.store.service.BookService
import com.bookstore.mael.store.service.PurchaseService
import io.mockk.every
import io.mockk.mockk
import io.mockk.just
import io.mockk.Runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.scheduling.annotation.Async
import org.springframework.context.event.EventListener
import java.math.BigDecimal
import java.util.UUID

class PurchaseListenersTest {

    @Test
    fun `NFe listener generates a UUID and updates the same purchase`() {
        val service = mockk<PurchaseService>()
        val listener = GenerateNfeListener(service)
        val purchase = samplePurchase()
        every { service.update(purchase) } just Runs

        listener.listen(PurchaseEvent(this, purchase))

        assertNotNull(purchase.nfe)
        assertTrue(runCatching { UUID.fromString(purchase.nfe) }.isSuccess)
        verify(exactly = 1) { service.update(purchase) }
        assertAsyncEventListener(GenerateNfeListener::class.java)
    }

    @Test
    fun `sold book listener delegates the books from the purchase`() {
        val bookService = mockk<BookService>()
        val listener = UpdateSoldBookListener(bookService)
        val purchase = samplePurchase()
        every { bookService.purchase(purchase.books) } just Runs

        listener.listen(PurchaseEvent(this, purchase))

        verify(exactly = 1) { bookService.purchase(purchase.books) }
        assertAsyncEventListener(UpdateSoldBookListener::class.java)
    }

    private fun assertAsyncEventListener(listenerClass: Class<*>) {
        val method = listenerClass.getMethod("listen", PurchaseEvent::class.java)
        assertTrue(method.isAnnotationPresent(Async::class.java))
        assertTrue(method.isAnnotationPresent(EventListener::class.java))
    }

    private fun samplePurchase() = PurchaseModel(
        customer = CustomerModel(
            id = 10,
            name = "Customer",
            email = "customer@example.com",
            status = CustomerStatus.ATIVO,
            password = "unused"
        ),
        books = mutableListOf(),
        price = BigDecimal("20.00")
    )
}
