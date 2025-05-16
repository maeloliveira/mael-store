package com.bookstore.mael.store.service

import com.bookstore.mael.store.exception.CustomerNotFoundException
import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.repository.CustomerRepository
import org.springframework.stereotype.Service

@Service
class CustomerService(
    val customerRepository: CustomerRepository
) {

    fun getAll(name: String?): List<CustomerModel> {
        name?.let {
           return customerRepository.findByNameContaining(it)
        }

        return customerRepository.findAll().toList()
    }

    fun createCustomer(customer: CustomerModel) {

        customerRepository.save(customer)
    }

    fun getCustomerById(id: Int): CustomerModel {
        return customerRepository.findById(id).orElseThrow()
    }

    fun updateCustomer(customer: CustomerModel) {
        if (!customerRepository.existsById(customer.id!!)) {
            throw CustomerNotFoundException(customer.id!!)
        }

        customerRepository.save(customer)
    }

    fun deleteCustomer(id: Int) {

        if (!customerRepository.existsById(id)) {
            throw CustomerNotFoundException(id)
        }

        customerRepository.deleteById(id)
    }

}