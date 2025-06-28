package com.bookstore.mael.store.controller

import com.bookstore.mael.store.controller.request.PostCustomerRequest
import com.bookstore.mael.store.controller.request.PutCustomerRequest
import com.bookstore.mael.store.controller.response.CustomerResponse
import com.bookstore.mael.store.extension.toCustomerModel
import com.bookstore.mael.store.extension.toResponse
import com.bookstore.mael.store.service.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("customer")
class CustomerController (
    val customerService : CustomerService
) {


    @GetMapping
    fun getAll(@RequestParam name : String? ) : List<CustomerResponse>{
        return customerService.getAll(name).map { it.toResponse()}
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCustomer(@RequestBody customer: PostCustomerRequest) {
        customerService.createCustomer(customer.toCustomerModel())
    }

    @GetMapping("/{id}")
    fun getCustomer(@PathVariable id: Int): CustomerResponse {
       return customerService.findById(id).toResponse()
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateCustomer(@PathVariable id: Int, @RequestBody customer: PutCustomerRequest) {
       val customerSaverd = customerService.findById(id)
        customerService.updateCustomer(customer.toCustomerModel(customerSaverd))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCustomer(@PathVariable id: Int) {
        customerService.deleteCustomer(id)
    }

}