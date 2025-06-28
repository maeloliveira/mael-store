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
data class BookModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "name")
    var name: String,

    @Column(name = "price")
    var price: BigDecimal,

    @ManyToOne
    @JoinColumn
    var customer: CustomerModel? = null

) {
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: BookStatus? = null
        set(value) {
            if (field == BookStatus.DELETADO || field == BookStatus.CANCELADO)
                throw Exception("Erro ao processar requisição com status: ${field}")

            field = value
        }

    constructor(
        id: Int? = null,
        name: String,
        price: BigDecimal,
        customer: CustomerModel? = null,
        status: BookStatus?
    ) : this(id, name, price, customer) {
        this.status = status
    }
}