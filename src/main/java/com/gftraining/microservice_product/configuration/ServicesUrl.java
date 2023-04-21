package com.gftraining.microservice_product.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "services-url")
@Configuration
public class ServicesUrl {
    private String cartUrl;
    private String userUrl;
}
