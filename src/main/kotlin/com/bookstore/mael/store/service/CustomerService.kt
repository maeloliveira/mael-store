package com.bookstore.mael.store.service

import com.bookstore.mael.store.enum.CustomerStatus
import com.bookstore.mael.store.exception.CustomerNotFoundException
import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.repository.CustomerRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class CustomerService(
    val customerRepository: CustomerRepository,
    val bookService: BookService
) {

    private val logger = KotlinLogging.logger {}

    fun getAll(name: String?): List<CustomerModel> {
        name?.let {
            return customerRepository.findByNameContaining(it)
        }

        return customerRepository.findAll().toList()
    }

    fun createCustomer(customer: CustomerModel) {

        customerRepository.save(customer)
    }

    fun findById(id: Int): CustomerModel {
        return customerRepository.findById(id).orElseThrow()
    }

    fun updateCustomer(customer: CustomerModel) {
        if (!customerRepository.existsById(customer.id!!)) {
            throw CustomerNotFoundException(customer.id!!)
        }

        customerRepository.save(customer)
    }

    fun deleteCustomer(id: Int) {

        val customer = findById(id)
        bookService.deleteByCustomer(customer)
        customer.status = CustomerStatus.INATIVO
        customerRepository.save(customer)
        logger.info{"Customer ${customer.name} was completed deleted"}
    }

}