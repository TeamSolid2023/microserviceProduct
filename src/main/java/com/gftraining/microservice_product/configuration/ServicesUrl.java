package com.gftraining.microservice_product.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix="services-url")
public class ServicesUrl {
    private String cartUrl;
    private String userUrl;
}
