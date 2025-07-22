package com.bookstore.mael.store.service

import com.bookstore.mael.store.events.PurchaseEvent
import com.bookstore.mael.store.model.PurchaseModel
import com.bookstore.mael.store.repository.PurchaseRepository
import org.springframework.context.ApplicationEventPublisher

import org.springframework.stereotype.Service

@Service
class PurchaseService (
    private val purchaseRepository: PurchaseRepository,
    private val aplicationEventPuplisher: ApplicationEventPublisher
){

    fun create(purchaseModel: PurchaseModel){
        purchaseRepository.save(purchaseModel)

        aplicationEventPuplisher.publishEvent(PurchaseEvent(this, purchaseModel))
    }

    fun update(purchaseModel: PurchaseModel) {
        purchaseRepository.save(purchaseModel)
    }
}
