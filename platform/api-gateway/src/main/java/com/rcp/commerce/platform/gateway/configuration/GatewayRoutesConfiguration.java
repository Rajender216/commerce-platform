package com.rcp.commerce.platform.gateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GatewayRoutesConfiguration {

    @Bean
    RouteLocator commerceRoutes(RouteLocatorBuilder builder, GatewayProperties properties) {
        return builder.routes()
                .route("catalog-public-categories", route -> route.method("GET")
                        .and()
                        .path("/api/v1/categories")
                        .uri(properties.getRoutes().getCatalogUri()))
                .route("catalog-public-products", route -> route.method("GET")
                        .and()
                        .path("/api/v1/products", "/api/v1/products/**")
                        .uri(properties.getRoutes().getCatalogUri()))
                .route("catalog-admin-categories", route -> route.path(
                                "/api/v1/admin/categories", "/api/v1/admin/categories/**")
                        .uri(properties.getRoutes().getCatalogUri()))
                .route("catalog-admin-products", route -> route.path(
                                "/api/v1/admin/products", "/api/v1/admin/products/**")
                        .uri(properties.getRoutes().getCatalogUri()))
                .route("customer-api", route -> route.path("/api/v1/customers", "/api/v1/customers/**")
                        .uri(properties.getRoutes().getCustomerUri()))
                .build();
    }
}
