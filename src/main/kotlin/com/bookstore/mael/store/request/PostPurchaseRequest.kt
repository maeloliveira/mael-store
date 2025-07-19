package com.bookstore.mael.store.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class PostPurchaseRequest(
    @field:NotNull
    @field:Positive
    val costumerId: Int,

    @field:NotNull
    var booId: Set<Int>


)
