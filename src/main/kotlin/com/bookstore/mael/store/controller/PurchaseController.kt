package com.bookstore.mael.store.controller

import com.bookstore.mael.store.controller.mapper.PurchaseMapper
import com.bookstore.mael.store.request.PostPurchaseRequest
import com.bookstore.mael.store.service.PurchaseService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("purchase")
class PurchaseController(
    private val purchaseService: PurchaseService,
    private val purchaseMapper: PurchaseMapper
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun purchase(@RequestBody request: PostPurchaseRequest) {
        purchaseService.create(purchaseMapper.toModel(request))
    }

}