package com.github.dennispoliciano.escalas;

import java.time.Duration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withStartupTimeout(Duration.ofSeconds(120))
            .withReuse(true);

    static {
        postgres.start();
    }
}
