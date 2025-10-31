package com.bookstore.mael.store

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class MaelStoreApplication

fun main(args: Array<String>) {
	runApplication<MaelStoreApplication>(*args)
}

