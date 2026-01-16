package com.bookstore.mael.store

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = ["spring.flyway.enabled=false"])
class MaelStoreApplicationTests {

	@Test
	fun contextLoads() {
	}

}
