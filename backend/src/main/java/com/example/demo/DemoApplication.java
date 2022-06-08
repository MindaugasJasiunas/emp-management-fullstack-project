package com.example.demo;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.UUID;
@EnableConfigurationProperties(JWTTokenProvider.class)
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
