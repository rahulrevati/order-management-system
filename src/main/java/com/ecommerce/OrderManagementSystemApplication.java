package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Slf4j
@EnableAsync
public class OrderManagementSystemApplication {

	public static void main(String[] args) {
		log.info("Starting Order Management System Application...");
		SpringApplication.run(OrderManagementSystemApplication.class, args);
		log.info("Order Management System Application started successfully");
	}

}
