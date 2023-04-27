package com.gftraining.microservice_product.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "feature-flags")
public class FeatureFlagsConfig {
    private boolean callUserEnabled;
    private boolean callCartEnabled;
}
