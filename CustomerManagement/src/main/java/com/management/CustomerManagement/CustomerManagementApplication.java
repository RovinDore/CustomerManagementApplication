package com.management.CustomerManagement;

import com.management.CustomerManagement.Config.MyUser;
import com.management.CustomerManagement.Models.AuthenticationResponse;
import com.management.CustomerManagement.Models.SignupRequest;
import com.management.CustomerManagement.Models.StorageProperties;
import com.management.CustomerManagement.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableAsync
public class CustomerManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerManagementApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserService userService) {
		return (args) -> {
			//Create default Admin account
			MyUser adminExists = userService.loadUsernameNoException("admin");
			if(adminExists == null){
				SignupRequest signupRequest = new SignupRequest("admin", "admin@msn.com", "password");
				userService.createAdmin(signupRequest);
			}

		};
	}
}
