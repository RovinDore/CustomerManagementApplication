package com.management.CustomerManagement;

import com.management.CustomerManagement.Models.StorageProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootTest
@EnableAsync
@EnableConfigurationProperties(StorageProperties.class)
class CustomerManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
