package com.bookstore.mael.store.controller.request

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class PostBookRequest (
    @field: NotEmpty(message = "Field name can't be empty")
    var name: String,

    @field:NotNull(message = "Field price can't be empty")
    var price: BigDecimal,

    @JsonAlias("customer_id")
    var customerId: Int

)