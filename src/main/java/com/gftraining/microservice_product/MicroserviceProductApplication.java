package com.gftraining.microservice_product;

import com.gftraining.microservice_product.configuration.CategoriesConfig;
import com.gftraining.microservice_product.configuration.FeatureFlagsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@Slf4j
@EnableConfigurationProperties({CategoriesConfig.class, FeatureFlagsConfig.class})
public class MicroserviceProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceProductApplication.class, args);
    }

}
