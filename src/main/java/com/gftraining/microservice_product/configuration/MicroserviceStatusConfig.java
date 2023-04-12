package com.gftraining.microservice_product.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="microservicestatus")
public class MicroserviceStatusConfig {
    private boolean user;
    private boolean cart;
}
