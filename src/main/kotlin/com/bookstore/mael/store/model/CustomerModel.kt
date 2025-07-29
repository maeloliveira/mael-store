package com.bookstore.mael.store.model

import com.bookstore.mael.store.enums.CustomerStatus
import com.bookstore.mael.store.enums.Profile
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import org.hibernate.query.sqm.FetchClauseType

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
    var status: CustomerStatus,

    @Column(name = "password")
    val password: String,


    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Profile::class, fetch = FetchType.EAGER)
    @CollectionTable(name = "customer_roles", joinColumns = [JoinColumn(name = "customer_id")])
    var roles: Set<Profile> = setOf()

)