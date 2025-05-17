package com.bookstore.mael.store.model

import com.bookstore.mael.store.enum.CustomerStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "customer")
data class CustomerModel (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Int? = null,

    @Column(name = "name")
    var name : String,

    @Column(name = "email")
    var email : String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: CustomerStatus

)