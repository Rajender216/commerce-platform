package com.rcp.commerce.platform.gateway.configuration;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("commerce.gateway")
public class GatewayProperties {

    private final Routes routes = new Routes();
    private final Correlation correlation = new Correlation();
    private final Cors cors = new Cors();

    public Routes getRoutes() {
        return routes;
    }

    public Correlation getCorrelation() {
        return correlation;
    }

    public Cors getCors() {
        return cors;
    }

    public static class Routes {

        private String catalogUri = "lb://catalog-service";
        private String customerUri = "lb://customer-service";

        public String getCatalogUri() {
            return catalogUri;
        }

        public void setCatalogUri(String catalogUri) {
            this.catalogUri = catalogUri;
        }

        public String getCustomerUri() {
            return customerUri;
        }

        public void setCustomerUri(String customerUri) {
            this.customerUri = customerUri;
        }
    }

    public static class Correlation {

        private String headerName = "X-Correlation-ID";
        private int maxLength = 128;

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public static class Cors {

        private List<String> allowedOrigins = List.of("http://localhost:4200");
        private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        private List<String> allowedHeaders = List.of("Authorization", "Content-Type", "X-Correlation-ID");
        private List<String> exposedHeaders = List.of("X-Correlation-ID", "Location");

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public List<String> getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(List<String> allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public List<String> getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(List<String> allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public List<String> getExposedHeaders() {
            return exposedHeaders;
        }

        public void setExposedHeaders(List<String> exposedHeaders) {
            this.exposedHeaders = exposedHeaders;
        }
    }
}
