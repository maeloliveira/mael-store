package com.bookstore.mael.store.repository

import com.bookstore.mael.store.model.CustomerModel
import org.springframework.data.repository.CrudRepository

interface CustomerRepository : CrudRepository<CustomerModel, Int>{

    fun findByNameContaining(name: String): List<CustomerModel>

}