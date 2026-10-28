package com.bookstore.mael.store.controller

import com.bookstore.mael.store.controller.mapper.PurchaseMapper
import com.bookstore.mael.store.enums.CustomerStatus
import com.bookstore.mael.store.exception.ControllerAdvice
import com.bookstore.mael.store.exception.NotFoundException
import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.model.PurchaseModel
import com.bookstore.mael.store.service.PurchaseService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal

class PurchaseControllerTest {

    @Test
    fun `POST purchase accepts snake case aliases and returns 201 without body`() {
        val mapper = mockk<PurchaseMapper>()
        val service = mockk<PurchaseService>(relaxed = true)
        val requestSlot = slot<com.bookstore.mael.store.request.PostPurchaseRequest>()
        val purchase = samplePurchase()
        every { mapper.toModel(capture(requestSlot)) } returns purchase

        mvc(mapper, service).perform(
            post("/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"customer_id": 10, "book_ids": [21, 22]}""")
        )
            .andExpect(status().isCreated)
            .andExpect(content().string(""))

        assertEquals(10, requestSlot.captured.customerId)
        assertEquals(setOf(21, 22), requestSlot.captured.bookIds)
        verify(exactly = 1) { service.create(purchase) }
    }

    @Test
    fun `POST purchase maps not found errors to 404 response`() {
        val mapper = mockk<PurchaseMapper>()
        val service = mockk<PurchaseService>(relaxed = true)
        every { mapper.toModel(any()) } throws NotFoundException("Book [22] not exists", "ML-101")

        mvc(mapper, service).perform(
            post("/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"customer_id": 10, "book_ids": [22]}""")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.httpCode").value(404))
            .andExpect(jsonPath("$.internalCode").value("ML-101"))
            .andExpect(jsonPath("$.message").value("Book [22] not exists"))
    }

    private fun mvc(mapper: PurchaseMapper, service: PurchaseService): MockMvc =
        MockMvcBuilders.standaloneSetup(PurchaseController(service, mapper))
            .setControllerAdvice(ControllerAdvice())
            .setMessageConverters(
                MappingJackson2HttpMessageConverter(jacksonObjectMapper())
            )
            .build()

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
