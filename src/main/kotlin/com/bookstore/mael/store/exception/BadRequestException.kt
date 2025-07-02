package com.bookstore.mael.store.exception

class BadRequestException(override val message: String, val errorCode: String) : Exception() {


}
