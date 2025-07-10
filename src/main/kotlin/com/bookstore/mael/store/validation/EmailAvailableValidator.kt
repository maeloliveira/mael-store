package com.bookstore.mael.store.validation

import com.bookstore.mael.store.service.CustomerService
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

class EmailAvailableValidator(var customerService: CustomerService): ConstraintValidator<EmailAvailable, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if(value.isNullOrEmpty()){
            return false
        }
        return customerService.emailAvailable(value)
    }


}
