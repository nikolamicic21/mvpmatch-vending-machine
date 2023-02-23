package io.nikolamicic21.mvpmatchvendingmachine.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Base64;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public abstract class AbstractIntegrationTest {

    public static final String BUYER_1_USERNAME = "user1";
    public static final String BUYER_1_PASSWORD = "password1";
    public static final String BUYER_1_USERNAME_UPDATED = "user1-updated";
    public static final String BUYER_1_PASSWORD_UPDATED = "password1-updated";

    static final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    static PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:15.2")
                .withPassword("mvpmatch")
                .withUsername("mvpmatch")
                .withDatabaseName("mvpmatch");
        postgres.start();
    }

    @LocalServerPort
    Integer port;
    WebTestClient client;

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    static String buildBasicAuthorizationHeader(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    @BeforeEach
    void setUp() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    void clearDatabaseData(JdbcTemplate jdbcTemplate) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "mvpmatch_product", "mvpmatch_user");
    }

    void clearSessionRegistryAndSecurityContext(SessionRegistry sessionRegistry) {
        sessionRegistry.getAllPrincipals().forEach(principle -> {
            for (final var sessionInformation : sessionRegistry.getAllSessions(principle, true)) {
                sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
            }
        });
        securityContextHolderStrategy.clearContext();
    }

    @AfterEach
    void tearDown(@Autowired JdbcTemplate jdbcTemplate, @Autowired SessionRegistry sessionRegistry) {
        clearDatabaseData(jdbcTemplate);
        clearSessionRegistryAndSecurityContext(sessionRegistry);
    }
}
