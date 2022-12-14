package com.ProductPassive.ProductPassive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ProductPassiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductPassiveApplication.class, args);
	}

}
