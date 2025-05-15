package com.bookstore.mael.store.model

import com.bookstore.mael.store.enum.BookStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

@Entity(name = "book")
data class BookModel (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Int? = null,

    @Column(name = "name")
    var name : String,

    @Column(name = "price")
    var price : BigDecimal,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status : BookStatus? =null,

    @ManyToOne
    @JoinColumn
    var customer: CustomerModel? = null

)