package com.bookstore.mael.store.controller

import com.bookstore.mael.store.CustomerModel
import com.bookstore.mael.store.controller.request.PostCustomerRequest
import com.bookstore.mael.store.controller.request.PutCustomerRequest
import com.bookstore.mael.store.extension.toCustomerModel
import com.bookstore.mael.store.service.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("customer")
class CustomerController (
    val customerService : CustomerService
) {


    @GetMapping
    fun getAll(@RequestParam name : String? ) : List<CustomerModel>{
        return customerService.getAll(name)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCustomer(@RequestBody customer: PostCustomerRequest) {
        customerService.createCustomer(customer.toCustomerModel())
    }

    @GetMapping("/{id}")
    fun getCustomer(@PathVariable id: String): CustomerModel {
       return customerService.getCustomer(id)
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateCustomer(@PathVariable id: String, @RequestBody customer: PutCustomerRequest) {
       customerService.updateCustomer(customer.toCustomerModel(id))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCustomer(@PathVariable id: String) {
        customerService.deleteCustomer(id)
    }

}