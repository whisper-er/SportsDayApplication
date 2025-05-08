package com.example.demo;

import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SportsDayApplicationTests {
	@MockBean
	private UserService userService;

	@Test
	void contextLoads() {
		SportsDayApplication.main(new String[] {});
	}

}
