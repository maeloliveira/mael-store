package com.bookstore.mael.store.enums

enum class Errors (val code: String, val message: String ){
    ML001("ML-001","Invalid arguments "),
    ML101("ML-101", "Book [%s] not exists"),
    ML102("ML-102", "Cannot update Book with status [%s]"),
    ML202("ML-202", "Customer [%s] not exists")

}