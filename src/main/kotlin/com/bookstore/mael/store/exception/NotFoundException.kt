package com.bookstore.mael.store.exception

class NotFoundException(override val message: String, val errorCode: String) : Exception() {


}
