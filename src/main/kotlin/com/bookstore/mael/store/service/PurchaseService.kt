package com.bookstore.mael.store.service

import com.bookstore.mael.store.events.PurchaseEvent
import com.bookstore.mael.store.model.PurchaseModel
import com.bookstore.mael.store.repository.PurchaseRepository
import org.hibernate.internal.CoreLogging.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class PurchaseService (
    private val purchaseRepository: PurchaseRepository,
    private val aplicationEventPublisher: ApplicationEventPublisher
){

    fun create(purchaseModel: PurchaseModel){
        purchaseRepository.save(purchaseModel)

        logger("Purchase").info("Starting purchase method")
        aplicationEventPublisher.publishEvent(PurchaseEvent(this, purchaseModel))
        logger("Purchase").info("Finish purchase")
    }

    fun update(purchaseModel: PurchaseModel) {
        purchaseRepository.save(purchaseModel)
    }
}
