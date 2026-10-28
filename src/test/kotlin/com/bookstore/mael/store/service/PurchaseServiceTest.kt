package com.bookstore.mael.store.service

import com.bookstore.mael.store.events.PurchaseEvent
import com.bookstore.mael.store.enums.CustomerStatus
import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.model.PurchaseModel
import com.bookstore.mael.store.repository.PurchaseRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal

class PurchaseServiceTest {

    private val repository = mockk<PurchaseRepository>()
    private val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
    private val service = PurchaseService(repository, eventPublisher)

    @Test
    fun `saves purchase before publishing event with the same model`() {
        val purchase = PurchaseModel(
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
        every { repository.save(purchase) } returns purchase

        service.create(purchase)

        verifyOrder {
            repository.save(purchase)
            eventPublisher.publishEvent(match<PurchaseEvent> {
                it.purchaseModel === purchase
            })
        }
    }
}
