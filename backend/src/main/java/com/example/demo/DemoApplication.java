package com.example.demo;

import com.example.demo.email.EmailConstant;
import com.example.demo.utility.JWTTokenProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({JWTTokenProvider.class, EmailConstant.class})
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner initAppData() {
		return args -> {
			// CommandLineRunner runs after the application context has been loaded
		};
	}
}
