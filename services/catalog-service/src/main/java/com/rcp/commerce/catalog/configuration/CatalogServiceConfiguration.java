package com.rcp.commerce.catalog.configuration;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CatalogServiceConfiguration {

    @Bean
    Clock utcClock() {
        return Clock.systemUTC();
    }
}
