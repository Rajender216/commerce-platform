package com.rcp.commerce.customer.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "customer")
public record CustomerServiceProperties(Correlation correlation) {

    public CustomerServiceProperties {
        if (correlation == null) {
            correlation = new Correlation("X-Correlation-ID", 128);
        }
    }

    public record Correlation(String headerName, int maxLength) {}
}
