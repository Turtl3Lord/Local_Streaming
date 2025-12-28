package com.StreamingServer.server.testMigrations;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class FlywayMigrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("test")
                    .withUsername("test")
                    .withPassword("test");

    @Test
    void migrationsMustRun() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        postgres.getJdbcUrl(),
                        postgres.getUsername(),
                        postgres.getPassword()
                )
                .load();

        flyway.migrate();
    }
}
