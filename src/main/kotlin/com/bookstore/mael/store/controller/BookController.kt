package com.bookstore.mael.store.controller

import com.bookstore.mael.store.controller.request.PostBookRequest
import com.bookstore.mael.store.controller.request.PutBookRequest
import com.bookstore.mael.store.controller.response.BookResponse
import com.bookstore.mael.store.extension.toBookModel
import com.bookstore.mael.store.extension.toResponse
import com.bookstore.mael.store.service.BookService
import com.bookstore.mael.store.service.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("books")
class BookController(
    val customerService: CustomerService,
    val bookService: BookService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBook(@RequestBody request: PostBookRequest) {
        val customer = customerService.findById(request.customerId)
        bookService.create(request.toBookModel(customer))
    }

    @GetMapping
    fun findAll(): List<BookResponse> =
        bookService.findAll().map { it.toResponse() }

    @GetMapping("{id}")
    fun getBookById(@PathVariable id: Int): BookResponse{
        return bookService.findById(id).toResponse()
    }

    @GetMapping("/active")
    fun findActives(): List<BookResponse> =
        bookService.findActives().map { it.toResponse() }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBook(@PathVariable id: Int) =
        bookService.deleteBook(id)

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updade(@PathVariable id: Int, @RequestBody book: PutBookRequest) {
        val bookSaved = bookService.findById(id)

        bookService.update(book.toBookModel(bookSaved))
    }


}