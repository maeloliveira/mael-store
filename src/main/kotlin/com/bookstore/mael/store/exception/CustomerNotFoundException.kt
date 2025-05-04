package com.bookstore.mael.store.exception

class CustomerNotFoundException(id: Int) : RuntimeException("Customer with id $id not found"){

}
