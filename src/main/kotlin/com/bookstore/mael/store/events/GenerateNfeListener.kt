package com.bookstore.mael.store.events

import com.bookstore.mael.store.service.PurchaseService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class GenerateNfeListener (
    private val purchaseService: PurchaseService
){

    @Async
    @EventListener
    fun listen (purchaseEvent: PurchaseEvent) {
        val nfe = UUID.randomUUID().toString()
        val purchaseModel = purchaseEvent.purchaseModel
        purchaseModel.nfe = nfe
        purchaseService.update(purchaseModel)
    }
}
