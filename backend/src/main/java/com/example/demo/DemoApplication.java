package com.example.demo;

import com.example.demo.email.EmailConstant;
import com.example.demo.resource.UserResource;
import com.example.demo.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
			createServerDirectories();
		};
	}

	void createServerDirectories() throws IOException {
		// in the server - create folder structure if doesn't exist
		Path root = Paths.get("").toAbsolutePath();
		Path path = Paths.get(root.toString(), File.separator, "application", File.separator, "profileImage");
		Files.createDirectories(path);
	}
}
