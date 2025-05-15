package com.bookstore.mael.store.exception

class BookNotFoundException(id: Int) : RuntimeException("Book with id $id not found"){

}
