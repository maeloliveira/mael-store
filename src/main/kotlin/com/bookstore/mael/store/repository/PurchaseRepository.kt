package com.bookstore.mael.store.repository

import com.bookstore.mael.store.model.PurchaseModel
import org.springframework.data.repository.CrudRepository

interface PurchaseRepository: CrudRepository<PurchaseModel, Int> {

}
