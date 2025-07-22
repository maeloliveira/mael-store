package com.bookstore.mael.store.request

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class PostPurchaseRequest(
    @field:NotNull
    @field:Positive
    @JsonAlias("customer_id")
    val costumerId: Int,

    @field:NotNull
    @JsonAlias("book_ids")
    var bookIds: Set<Int>


)
