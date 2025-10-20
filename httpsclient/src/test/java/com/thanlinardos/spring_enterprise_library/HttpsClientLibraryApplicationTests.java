package com.thanlinardos.spring_enterprise_library;

import com.thanlinardos.spring_enterprise_library.annotations.SpringTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SpringTest
class HttpsClientLibraryApplicationTests {

	@Test
	void contextLoads() {
		// This test is used to check if the application context loads successfully.
		Assertions.assertDoesNotThrow(() -> {});
	}
}
