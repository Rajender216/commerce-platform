package com.rcp.commerce.catalog.configuration;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "catalog")
public record CatalogServiceProperties(Correlation correlation) {

    public CatalogServiceProperties {
        if (correlation == null) {
            correlation = new Correlation("X-Correlation-ID", 128);
        }
    }

    public record Correlation(@NotBlank String headerName, @Min(16) @Max(256) int maxLength) {}
}
