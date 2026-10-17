package com.rcp.commerce.platform.discovery;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.core.env.Environment;

@SpringBootTest(
        properties = {"server.port=0", "eureka.client.register-with-eureka=false", "eureka.client.fetch-registry=false"
        })
class DiscoveryServerApplicationTest {

    @Autowired
    private Environment environment;

    @Test
    void contextStartsWithEurekaServerEnabled() {
        assertThat(DiscoveryServerApplication.class).hasAnnotation(EnableEurekaServer.class);
        assertThat(environment.getProperty("spring.application.name")).isEqualTo("discovery-server");
    }

    @Test
    void localServerDoesNotRegisterWithItselfOrFetchRegistry() {
        assertThat(environment.getProperty("eureka.client.register-with-eureka", Boolean.class))
                .isFalse();
        assertThat(environment.getProperty("eureka.client.fetch-registry", Boolean.class))
                .isFalse();
    }

    @Test
    void exposesOnlyLocalMvpActuatorEndpoints() {
        assertThat(environment.getProperty("management.endpoints.web.exposure.include"))
                .isEqualTo("health,info");
    }
}
