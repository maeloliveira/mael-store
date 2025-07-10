package com.bookstore.mael.store.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [EmailAvailableValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class EmailAvailable(
    val message: String = "Email in use",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
