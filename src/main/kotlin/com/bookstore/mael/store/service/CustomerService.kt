package com.bookstore.mael.store.service

import com.bookstore.mael.store.enums.CustomerStatus
import com.bookstore.mael.store.enums.Errors
import com.bookstore.mael.store.enums.Profile
import com.bookstore.mael.store.exception.NotFoundException
import com.bookstore.mael.store.model.CustomerModel
import com.bookstore.mael.store.repository.CustomerRepository
import mu.KotlinLogging
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
    @Lazy
    private val bookService: BookService,
    private val bCrypt: BCryptPasswordEncoder
) {

    private val logger = KotlinLogging.logger {}

    fun getAll(name: String?, pageable: Pageable): Page<CustomerModel> {
        name?.let {
            return customerRepository.findByNameContaining(it, pageable)
        }

        return customerRepository.findAll(pageable)
    }

    fun createCustomer(customer: CustomerModel) {

        val customerCopy = customer.copy(
            roles = setOf(Profile.CUSTOMER),
            password = bCrypt.encode(customer.password)
        )
        customerRepository.save(customerCopy)
    }

    fun findById(id: Int): CustomerModel {
        return customerRepository.findById(id).orElseThrow {
            NotFoundException(Errors.ML202.message.format(id), Errors.ML202.code)
        }
    }

    fun updateCustomer(customer: CustomerModel) {
        if (!customerRepository.existsById(customer.id!!)) {
            throw NotFoundException(Errors.ML202.message.format(customer.id!!), Errors.ML202.code)

        }

        customerRepository.save(customer)
    }

    fun deleteCustomer(id: Int) {

        val customer = findById(id)
        bookService.deleteByCustomer(customer)
        customer.status = CustomerStatus.INATIVO
        customerRepository.save(customer)
        logger.info { "Customer ${customer.name} was completed deleted" }
    }

    fun emailAvailable(email: String): Boolean {
       return !customerRepository.existsByEmail(email)
    }

}