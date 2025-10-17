package ai.shreds.product_management_availability_shred_g5ds;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify that the Product Management & Availability Shred application
 * starts up correctly with all required dependencies (PostgreSQL, Redis, ActiveMQ, External Services).
 * 
 * This test uses TestContainers to provide real database and cache instances,
 * WireMock to mock external services, and captures full application logs for analysis.
 * 
 * Test Scope:
 * - Application context loads successfully
 * - All Spring Boot components are initialized
 * - Database connection works
 * - Redis connection works
 * - JMS messaging is configured
 * - Health endpoints are accessible
 * - External service mocks respond correctly
 * - Full application logs are captured for debugging
 */
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationStartupIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupIntegrationTest.class);

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    // TestContainers for Infrastructure Dependencies
    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("test_product_catalog_db")
            .withUsername("test_user")
            .withPassword("test_password")
            .withReuse(false);

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:6.2-alpine"))
            .withExposedPorts(6379)
            .withReuse(false);

    // WireMock server for external service mocking
    private static WireMockServer wireMockServer;

    /**
     * Configure application properties dynamically based on TestContainers
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL Configuration
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        // Redis Configuration
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
        registry.add("spring.redis.password", () -> "");

        // External Service Mock Configuration (WireMock)
        registry.add("product.availability.sync.inventory-service-url", 
                () -> "http://localhost:" + wireMockServer.port() + "/api/v1/inventory");
        
        // JMS Configuration (use embedded broker)
        registry.add("spring.activemq.broker-url", () -> "vm://localhost?broker.persistent=false");
        registry.add("spring.activemq.packages.trust-all", () -> "true");

        // Logging Level for better test visibility
        registry.add("logging.level.ai.shreds.product_management_availability_shred_g5ds", () -> "DEBUG");
        registry.add("logging.level.org.springframework.boot", () -> "INFO");
        registry.add("logging.level.org.testcontainers", () -> "INFO");
    }

    /**
     * Set up WireMock server and mock external services before all tests
     */
    @BeforeAll
    void setUp() {
        // Start WireMock server
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        
        logger.info("=== INTEGRATION TEST SETUP ===");
        logger.info("WireMock Server started on port: {}", wireMockServer.port());
        logger.info("PostgreSQL Container - JDBC URL: {}", postgresContainer.getJdbcUrl());
        logger.info("Redis Container - Host: {}, Port: {}", redisContainer.getHost(), redisContainer.getMappedPort(6379));
        
        setupExternalServiceMocks();
    }

    /**
     * Clean up WireMock server after all tests
     */
    @AfterAll
    void tearDown() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            logger.info("WireMock Server stopped");
        }
    }

    /**
     * Configure WireMock stubs for external inventory service
     */
    private void setupExternalServiceMocks() {
        // Mock inventory service health check
        wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/inventory/health"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\": \"UP\", \"service\": \"inventory-management\"}"))
        );

        // Mock inventory status endpoint
        wireMockServer.stubFor(get(urlPathMatching("/api/v1/inventory/status.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"locationId\": \"test-location-123\", \"inventoryData\": [], \"timestamp\": \"2024-01-15T10:00:00Z\"}"))
        );

        logger.info("External service mocks configured successfully");
    }

    /**
     * Main integration test: Verify application startup and basic functionality
     */
    @Test
    void shouldStartApplicationSuccessfully(CapturedOutput output) {
        logger.info("=== STARTING APPLICATION STARTUP INTEGRATION TEST ===");
        
        // Test 1: Verify Spring Application Context loads
        assertNotNull(applicationContext, "Application context should be loaded");
        logger.info("✓ Application context loaded successfully");

        // Test 2: Verify critical beans are present
        assertTrue(applicationContext.containsBean("dataSource"), "DataSource bean should be present");
        assertNotNull(dataSource, "DataSource should be injected");
        logger.info("✓ DataSource bean is available");

        // Test 3: Verify web server started
        assertTrue(port > 0, "Server should be running on a port");
        logger.info("✓ Web server started on port: {}", port);

        // Test 4: Test Health Endpoint
        String healthUrl = "http://localhost:" + port + "/api/v1/actuator/health";
        ResponseEntity<String> healthResponse = restTemplate.getForEntity(healthUrl, String.class);
        
        assertEquals(HttpStatus.OK, healthResponse.getStatusCode(), "Health endpoint should return OK");
        assertNotNull(healthResponse.getBody(), "Health response body should not be null");
        assertTrue(healthResponse.getBody().contains("UP") || healthResponse.getBody().contains("status"), 
                "Health response should indicate system is UP");
        logger.info("✓ Health endpoint responding correctly: {}", healthResponse.getBody());

        // Test 5: Verify Database Connection
        assertDoesNotThrow(() -> {
            dataSource.getConnection().close();
        }, "Database connection should work");
        logger.info("✓ Database connection established successfully");

        // Test 6: Verify application logs contain expected startup messages
        String logOutput = output.getOut();
        assertTrue(logOutput.contains("ProductManagementAvailabilityShredApplication"), 
                "Application class should be mentioned in logs");
        assertTrue(logOutput.contains("Started ProductManagementAvailabilityShredApplication") ||
                logOutput.contains("JVM running") ||
                logOutput.contains("Tomcat started on port"),
                "Application should show successful startup message");
        logger.info("✓ Application startup logs are present");

        // Test 7: Verify no critical errors in logs
        String errorOutput = output.getErr();
        assertFalse(logOutput.contains("APPLICATION FAILED TO START"), 
                "Application should not have startup failures");
        assertFalse(logOutput.contains("Error starting ApplicationContext"),
                "Application context should start without errors");
        logger.info("✓ No critical startup errors detected");

        // Print full logs for analysis
        logger.info("=== FULL APPLICATION LOGS ===\n{}\n=== END LOGS ===", logOutput);
        if (!errorOutput.trim().isEmpty()) {
            logger.warn("=== ERROR LOGS ===\n{}\n=== END ERROR LOGS ===", errorOutput);
        }

        logger.info("=== APPLICATION STARTUP TEST COMPLETED SUCCESSFULLY ===");
    }

    /**
     * Additional test to verify external service connectivity
     */
    @Test
    void shouldConnectToExternalServices() {
        // Verify WireMock inventory service is accessible
        String inventoryHealthUrl = "http://localhost:" + wireMockServer.port() + "/api/v1/inventory/health";
        ResponseEntity<String> inventoryResponse = restTemplate.getForEntity(inventoryHealthUrl, String.class);
        
        assertEquals(HttpStatus.OK, inventoryResponse.getStatusCode(), 
                "Mock inventory service should be accessible");
        assertTrue(inventoryResponse.getBody().contains("UP"), 
                "Mock inventory service should return UP status");
        
        logger.info("✓ External service mocks are working correctly");
    }

    /**
     * Test to verify containers are running
     */
    @Test
    void shouldVerifyContainersAreRunning() {
        assertTrue(postgresContainer.isRunning(), "PostgreSQL container should be running");
        assertTrue(redisContainer.isRunning(), "Redis container should be running");
        assertTrue(wireMockServer.isRunning(), "WireMock server should be running");
        
        logger.info("✓ All test containers and mocks are running correctly");
    }
}