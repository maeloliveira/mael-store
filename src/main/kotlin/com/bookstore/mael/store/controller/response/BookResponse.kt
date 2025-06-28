package com.bookstore.mael.store.controller.response

import com.bookstore.mael.store.enum.BookStatus
import com.bookstore.mael.store.model.CustomerModel
import java.math.BigDecimal

data class BookResponse (
    var id: Int? = null,    
    var name: String,
    var price: BigDecimal,
    var customer: CustomerModel? = null,
    val status: BookStatus? = null
)
