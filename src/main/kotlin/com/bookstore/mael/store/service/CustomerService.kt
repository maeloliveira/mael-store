package com.bookstore.mael.store.service

import com.bookstore.mael.store.CustomerModel
import org.springframework.stereotype.Service

@Service
class CustomerService {

    val customers = mutableListOf<CustomerModel>()

    fun getAll(name: String?): List<CustomerModel> {
        name?.let {
            return customers.filter { it.name.contains(name, ignoreCase = true) }
        }
        return customers
    }

    fun createCustomer(customer: CustomerModel) {

        val id = if (customers.isEmpty()) {
            1
        } else {
            customers.last().id!!.toInt() + 1
        }.toString()

        customer.id = id

        customers.add(customer)
    }

    fun getCustomer(id: String): CustomerModel {
        return customers.first { it.id == id }
    }

    fun updateCustomer(customer: CustomerModel)  {
        customers.first { it.id == customer.id }.let {
            it.name = customer.name
            it.email = customer.email
        }

    }

    fun deleteCustomer(id: String) {
        customers.removeIf { it.id == id }
    }

}