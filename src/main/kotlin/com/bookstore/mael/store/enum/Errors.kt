package com.bookstore.mael.store.enum

enum class Errors (val code: String, val message: String ){
    ML101("ML-0001", "Book [%s] not exists"),
    ML202("ML-0002", "Customer [%s] not exists")

}