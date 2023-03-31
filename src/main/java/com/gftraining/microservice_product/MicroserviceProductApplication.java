package com.gftraining.microserviceProduct;

import lombok.extern.slf4j.Slf4j;
import com.gftraining.microserviceProduct.configuration.Categories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@Slf4j
@EnableConfigurationProperties(Categories.class)
public class MicroserviceProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceProductApplication.class, args);
	}

}
