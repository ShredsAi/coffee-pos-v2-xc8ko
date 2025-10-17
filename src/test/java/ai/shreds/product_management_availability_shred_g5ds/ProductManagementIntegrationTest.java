package ai.shreds.product_management_availability_shred_g5ds;

import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOCreateProduct;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOCreateVariant;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumProductType;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedPagedResultDTO;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedProductDTO;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueNutritionalInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Product Management workflows including creation, persistence, and event publishing.
 * 
 * This test verifies:
 * - Product creation via REST API with variants
 * - Database persistence of product and variant data
 * - JMS ProductCreatedEvent publishing
 * - Product retrieval with filtering and pagination
 * - End-to-end workflow from REST request to database and messaging
 * 
 * Uses TestContainers for real PostgreSQL and Redis instances.
 * Uses embedded ActiveMQ for JMS messaging testing.
 * Uses WireMock for external inventory service mocking.
 */
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
    "spring.jms.listener.auto-startup=true",
    "product.jms.queues.product-created=test.product.created"
})
public class ProductManagementIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(ProductManagementIntegrationTest.class);

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private ObjectMapper objectMapper;

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

        // External Service Mock Configuration
        registry.add("product.availability.sync.inventory-service-url", 
                () -> "http://localhost:" + wireMockServer.port() + "/api/v1/inventory");
        
        // JMS Configuration (use embedded broker)
        registry.add("spring.activemq.broker-url", () -> "vm://localhost?broker.persistent=false");
        registry.add("spring.activemq.packages.trust-all", () -> "true");

        // Logging Level for better test visibility
        registry.add("logging.level.ai.shreds.product_management_availability_shred_g5ds", () -> "DEBUG");
        registry.add("logging.level.org.springframework.jms", () -> "DEBUG");
        registry.add("logging.level.org.hibernate.SQL", () -> "DEBUG");
        registry.add("logging.level.root", () -> "INFO");
    }

    /**
     * Set up WireMock server and mock external services before all tests
     */
    @BeforeAll
    void setUp() {
        // Start WireMock server
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        
        logger.info("=== PRODUCT MANAGEMENT INTEGRATION TEST SETUP ===");
        logger.info("WireMock Server started on port: {}", wireMockServer.port());
        logger.info("PostgreSQL Container - JDBC URL: {}", postgresContainer.getJdbcUrl());
        logger.info("Redis Container - Host: {}, Port: {}", redisContainer.getHost(), redisContainer.getMappedPort(6379));
        
        setupExternalServiceMocks();
        setupTestData();
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
                        .withBody("{\"status\": \"UP\", \"service\": \"inventory-management\"}"));

        logger.info("External service mocks configured successfully");
    }

    /**
     * Set up test data including a category for product creation
     */
    private void setupTestData() {
        try {
            // Create a test category
            String categoryId = UUID.randomUUID().toString();
            String insertCategorySql = """
                INSERT INTO product_categories (category_id, name, description, display_order, is_active)
                VALUES (?, 'Test Beverages', 'Test category for beverages', 1, true)
                ON CONFLICT (category_id) DO NOTHING
                """;
            jdbcTemplate.update(insertCategorySql, UUID.fromString(categoryId));
            
            logger.info("Test category created with ID: {}", categoryId);
        } catch (Exception e) {
            logger.warn("Could not create test category, will create via application", e);
        }
    }

    /**
     * Main Integration Test: Test complete product creation workflow
     * including database persistence and JMS event publishing
     */
    @Test
    @Transactional
    void When_Product_Is_Created_Then_Product_Is_Persisted_And_Event_Is_Published(CapturedOutput output) throws Exception {
        logger.info("=== STARTING PRODUCT MANAGEMENT INTEGRATION TEST ===");
        
        // Step 1: Create a category first (required for product creation)
        UUID categoryId = createTestCategory();
        logger.info("✓ Test category created with ID: {}", categoryId);
        
        // Step 2: Set up JMS consumer to capture ProductCreated events
        CountDownLatch messageLatch = new CountDownLatch(1);
        String[] receivedMessage = new String[1];
        
        JMSContext jmsContext = connectionFactory.createContext();
        JMSConsumer consumer = jmsContext.createConsumer(
            jmsContext.createQueue("test.product.created"));
        
        consumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    receivedMessage[0] = textMessage.getText();
                    logger.info("Received JMS message: {}", receivedMessage[0]);
                    logger.info("Message type property: {}", textMessage.getStringProperty("messageType"));
                    messageLatch.countDown();
                }
            } catch (Exception e) {
                logger.error("Error processing JMS message", e);
            }
        });
        
        // Step 3: Prepare product creation request with variants
        ApplicationDTOCreateProduct productRequest = createProductRequest(categoryId);
        logger.info("✓ Product creation request prepared: {}", productRequest.getName());
        
        // Step 4: Send POST request to create product
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ApplicationDTOCreateProduct> requestEntity = new HttpEntity<>(productRequest, headers);
        
        String createProductUrl = "http://localhost:" + port + "/api/v1/products";
        logger.info("Sending POST request to: {}", createProductUrl);
        
        ResponseEntity<SharedProductDTO> response = restTemplate.postForEntity(
                createProductUrl, requestEntity, SharedProductDTO.class);
        
        // Step 5: Verify HTTP Response
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), 
                "Product creation should return HTTP 201 CREATED");
        assertNotNull(response.getBody(), "Response body should not be null");
        
        SharedProductDTO createdProduct = response.getBody();
        assertNotNull(createdProduct.getProductId(), "Created product should have an ID");
        assertEquals(productRequest.getName(), createdProduct.getName(), 
                "Product name should match request");
        assertEquals(productRequest.getProductType(), createdProduct.getProductType(), 
                "Product type should match request");
        assertEquals(categoryId, createdProduct.getCategoryId(), 
                "Category ID should match request");
        
        logger.info("✓ Product created successfully via REST API with ID: {}", createdProduct.getProductId());
        
        // Step 6: Verify Database Persistence - Product
        String productQuery = "SELECT name, product_type, category_id, base_price_amount, base_price_currency, is_active FROM products WHERE product_id = ?";
        List<Map<String, Object>> productRows = jdbcTemplate.queryForList(productQuery, createdProduct.getProductId());
        
        assertFalse(productRows.isEmpty(), "Product should be persisted in database");
        Map<String, Object> productRow = productRows.get(0);
        
        assertEquals(productRequest.getName(), productRow.get("name"), 
                "Database product name should match request");
        assertEquals(productRequest.getProductType().toString(), productRow.get("product_type"), 
                "Database product type should match request");
        assertEquals(categoryId, productRow.get("category_id"), 
                "Database category ID should match request");
        assertEquals(productRequest.getBasePrice().getAmount(), (BigDecimal) productRow.get("base_price_amount"), 
                "Database base price amount should match request");
        assertEquals(productRequest.getBasePrice().getCurrency(), productRow.get("base_price_currency"), 
                "Database base price currency should match request");
        assertTrue((Boolean) productRow.get("is_active"), 
                "Product should be active by default");
        
        logger.info("✓ Product persisted correctly in database");
        
        // Step 7: Verify Database Persistence - Variants
        String variantsQuery = "SELECT size_name, price_modifier_amount, price_modifier_currency, is_available FROM product_variants WHERE product_id = ? ORDER BY size_name";
        List<Map<String, Object>> variantRows = jdbcTemplate.queryForList(variantsQuery, createdProduct.getProductId());
        
        assertEquals(2, variantRows.size(), "Should have 2 variants persisted in database");
        
        // Verify Small variant
        Map<String, Object> smallVariant = variantRows.get(0);
        assertEquals("Small", smallVariant.get("size_name"));
        assertEquals(new BigDecimal("0.00"), (BigDecimal) smallVariant.get("price_modifier_amount"));
        assertEquals("USD", smallVariant.get("price_modifier_currency"));
        assertTrue((Boolean) smallVariant.get("is_available"));
        
        // Verify Large variant  
        Map<String, Object> largeVariant = variantRows.get(1);
        assertEquals("Large", largeVariant.get("size_name"));
        assertEquals(new BigDecimal("1.50"), (BigDecimal) largeVariant.get("price_modifier_amount"));
        assertEquals("USD", largeVariant.get("price_modifier_currency"));
        assertTrue((Boolean) largeVariant.get("is_available"));
        
        logger.info("✓ Product variants persisted correctly in database");
        
        // Step 8: Verify JMS Event Publishing
        boolean messageReceived = messageLatch.await(10, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Should receive ProductCreated JMS message within 10 seconds");
        assertNotNull(receivedMessage[0], "JMS message content should not be null");
        
        // Parse and verify JMS message content
        Map<String, Object> messagePayload = objectMapper.readValue(receivedMessage[0], Map.class);
        assertEquals("PRODUCT_CREATED", messagePayload.get("messageType"), 
                "JMS message should have correct message type");
        assertEquals(createdProduct.getProductId().toString(), messagePayload.get("productId"), 
                "JMS message should contain correct product ID");
        assertEquals(productRequest.getName(), messagePayload.get("productName"), 
                "JMS message should contain correct product name");
        assertEquals(categoryId.toString(), messagePayload.get("categoryId"), 
                "JMS message should contain correct category ID");
        assertEquals(productRequest.getProductType().toString(), messagePayload.get("productType"), 
                "JMS message should contain correct product type");
        assertNotNull(messagePayload.get("timestamp"), 
                "JMS message should contain timestamp");
        
        logger.info("✓ ProductCreated event published correctly via JMS: {}", messagePayload);
        
        // Step 9: Verify application logs contain expected messages
        String logOutput = output.getOut();
        assertTrue(logOutput.contains("Publishing product created message"), 
                "Logs should show product created message publishing");
        assertTrue(logOutput.contains("Successfully published product created message"), 
                "Logs should show successful message publishing");
        
        // Clean up JMS resources
        consumer.close();
        jmsContext.close();
        
        logger.info("=== PRODUCT MANAGEMENT INTEGRATION TEST COMPLETED SUCCESSFULLY ===");
        logger.info("✓ Product creation workflow verified end-to-end");
        logger.info("✓ Database persistence confirmed");
        logger.info("✓ JMS event publishing confirmed");
        logger.info("✓ All assertions passed");
    }
    
    /**
     * Integration Test: Test product retrieval with category filtering and pagination
     * This verifies the GET /api/v1/products endpoint with filtering and pagination capabilities
     */
    @Test
    @Transactional
    void When_Products_Are_Retrieved_With_Filters_Then_Correct_Paginated_Results_Are_Returned(CapturedOutput output) throws Exception {
        logger.info("=== STARTING PRODUCT RETRIEVAL WITH FILTERS TEST ===");
        
        // Step 1: Create test categories
        UUID beveragesCategoryId = createTestCategory("Test Beverages", "Test category for beverages", 1);
        UUID foodCategoryId = createTestCategory("Test Food", "Test category for food", 2);
        UUID merchandiseCategoryId = createTestCategory("Test Merchandise", "Test category for merchandise", 3);
        
        logger.info("✓ Test categories created: Beverages({}), Food({}), Merchandise({})", 
                beveragesCategoryId, foodCategoryId, merchandiseCategoryId);
        
        // Step 2: Create multiple test products in different categories
        List<UUID> createdProductIds = createMultipleTestProducts(beveragesCategoryId, foodCategoryId, merchandiseCategoryId);
        logger.info("✓ Created {} test products", createdProductIds.size());
        
        // Step 3: Test retrieval without filters - should return all products with pagination
        String getAllProductsUrl = "http://localhost:" + port + "/api/v1/products?page=0&size=5";
        logger.info("Testing product retrieval without filters: {}", getAllProductsUrl);
        
        ResponseEntity<SharedPagedResultDTO> allProductsResponse = restTemplate.getForEntity(
                getAllProductsUrl, SharedPagedResultDTO.class);
        
        assertEquals(HttpStatus.OK, allProductsResponse.getStatusCode(), 
                "Product retrieval should return HTTP 200 OK");
        assertNotNull(allProductsResponse.getBody(), "Response body should not be null");
        
        SharedPagedResultDTO<SharedProductDTO> allProductsPage = allProductsResponse.getBody();
        assertNotNull(allProductsPage.getContent(), "Products list should not be null");
        assertEquals(5, allProductsPage.getContent().size(), 
                "Should return 5 products per page");
        assertTrue(allProductsPage.getTotalElements() >= 7, 
                "Should have at least 7 total products");
        assertEquals(0, allProductsPage.getCurrentPage().intValue(), 
                "Current page should be 0");
        assertEquals(5, allProductsPage.getPageSize().intValue(), 
                "Page size should be 5");
        assertTrue(allProductsPage.getTotalPages() >= 2, 
                "Should have at least 2 pages");
        
        logger.info("✓ All products retrieval verified: {} total, {} pages", 
                allProductsPage.getTotalElements(), allProductsPage.getTotalPages());
        
        // Step 4: Test retrieval with category filter - beverages only
        String beverageProductsUrl = "http://localhost:" + port + "/api/v1/products?page=0&size=10&category=" + beveragesCategoryId;
        logger.info("Testing product retrieval with beverages category filter: {}", beverageProductsUrl);
        
        ResponseEntity<SharedPagedResultDTO> beverageResponse = restTemplate.getForEntity(
                beverageProductsUrl, SharedPagedResultDTO.class);
        
        assertEquals(HttpStatus.OK, beverageResponse.getStatusCode(), 
                "Beverage products retrieval should return HTTP 200 OK");
        assertNotNull(beverageResponse.getBody(), "Response body should not be null");
        
        SharedPagedResultDTO<SharedProductDTO> beveragePage = beverageResponse.getBody();
        assertNotNull(beveragePage.getContent(), "Beverage products list should not be null");
        assertEquals(3, beveragePage.getContent().size(), 
                "Should return 3 beverage products");
        assertEquals(3L, beveragePage.getTotalElements().longValue(), 
                "Should have exactly 3 beverage products");
        assertEquals(1, beveragePage.getTotalPages().intValue(), 
                "Should have 1 page for beverage products");
        
        // Verify all returned products are beverages
        List<Map<String, Object>> beverageProducts = (List<Map<String, Object>>) beveragePage.getContent();
        for (Map<String, Object> product : beverageProducts) {
            assertEquals(beveragesCategoryId.toString(), product.get("categoryId").toString(), 
                    "All returned products should be in beverages category");
            assertEquals("BEVERAGE", product.get("productType"), 
                    "All returned products should be BEVERAGE type");
        }
        
        logger.info("✓ Beverage category filter verified: {} products returned", beveragePage.getContent().size());
        
        // Step 5: Test retrieval with active filter
        String activeProductsUrl = "http://localhost:" + port + "/api/v1/products?page=0&size=10&active=true";
        logger.info("Testing product retrieval with active filter: {}", activeProductsUrl);
        
        ResponseEntity<SharedPagedResultDTO> activeResponse = restTemplate.getForEntity(
                activeProductsUrl, SharedPagedResultDTO.class);
        
        assertEquals(HttpStatus.OK, activeResponse.getStatusCode(), 
                "Active products retrieval should return HTTP 200 OK");
        assertNotNull(activeResponse.getBody(), "Response body should not be null");
        
        SharedPagedResultDTO<SharedProductDTO> activePage = activeResponse.getBody();
        assertNotNull(activePage.getContent(), "Active products list should not be null");
        assertTrue(activePage.getContent().size() >= 6, 
                "Should return at least 6 active products");
        assertTrue(activePage.getTotalElements() >= 6, 
                "Should have at least 6 total active products");
        
        // Verify all returned products are active
        List<Map<String, Object>> activeProducts = (List<Map<String, Object>>) activePage.getContent();
        for (Map<String, Object> product : activeProducts) {
            assertTrue((Boolean) product.get("isActive"), 
                    "All returned products should be active");
        }
        
        logger.info("✓ Active filter verified: {} active products returned", activePage.getContent().size());
        
        // Step 6: Test retrieval with combined filters - active beverages only
        String activeBeveragesUrl = "http://localhost:" + port + "/api/v1/products?page=0&size=10&category=" 
                + beveragesCategoryId + "&active=true";
        logger.info("Testing product retrieval with combined filters: {}", activeBeveragesUrl);
        
        ResponseEntity<SharedPagedResultDTO> activeBeveragesResponse = restTemplate.getForEntity(
                activeBeveragesUrl, SharedPagedResultDTO.class);
        
        assertEquals(HttpStatus.OK, activeBeveragesResponse.getStatusCode(), 
                "Active beverage products retrieval should return HTTP 200 OK");
        assertNotNull(activeBeveragesResponse.getBody(), "Response body should not be null");
        
        SharedPagedResultDTO<SharedProductDTO> activeBeveragesPage = activeBeveragesResponse.getBody();
        assertNotNull(activeBeveragesPage.getContent(), "Active beverage products list should not be null");
        assertEquals(3, activeBeveragesPage.getContent().size(), 
                "Should return 3 active beverage products");
        
        // Verify all returned products are active beverages
        List<Map<String, Object>> activeBeverageProducts = (List<Map<String, Object>>) activeBeveragesPage.getContent();
        for (Map<String, Object> product : activeBeverageProducts) {
            assertEquals(beveragesCategoryId.toString(), product.get("categoryId").toString(), 
                    "All returned products should be in beverages category");
            assertEquals("BEVERAGE", product.get("productType"), 
                    "All returned products should be BEVERAGE type");
            assertTrue((Boolean) product.get("isActive"), 
                    "All returned products should be active");
        }
        
        logger.info("✓ Combined filters verified: {} active beverage products returned", 
                activeBeveragesPage.getContent().size());
        
        // Step 7: Test pagination - get second page
        String secondPageUrl = "http://localhost:" + port + "/api/v1/products?page=1&size=5";
        logger.info("Testing pagination - second page: {}", secondPageUrl);
        
        ResponseEntity<SharedPagedResultDTO> secondPageResponse = restTemplate.getForEntity(
                secondPageUrl, SharedPagedResultDTO.class);
        
        assertEquals(HttpStatus.OK, secondPageResponse.getStatusCode(), 
                "Second page retrieval should return HTTP 200 OK");
        assertNotNull(secondPageResponse.getBody(), "Response body should not be null");
        
        SharedPagedResultDTO<SharedProductDTO> secondPage = secondPageResponse.getBody();
        assertNotNull(secondPage.getContent(), "Second page products list should not be null");
        assertTrue(secondPage.getContent().size() >= 2, 
                "Second page should have at least 2 products");
        assertEquals(1, secondPage.getCurrentPage().intValue(), 
                "Current page should be 1");
        assertEquals(5, secondPage.getPageSize().intValue(), 
                "Page size should be 5");
        
        logger.info("✓ Pagination verified: Second page has {} products", secondPage.getContent().size());
        
        // Step 8: Verify database queries are correct by checking logs
        String logOutput = output.getOut();
        assertTrue(logOutput.contains("SELECT"), 
                "Logs should show SQL SELECT queries");
        assertTrue(logOutput.contains("WHERE"), 
                "Logs should show SQL WHERE clauses for filtering");
        
        logger.info("=== PRODUCT RETRIEVAL WITH FILTERS TEST COMPLETED SUCCESSFULLY ===");
        logger.info("✓ Product retrieval without filters verified");
        logger.info("✓ Category filtering verified");
        logger.info("✓ Active status filtering verified");
        logger.info("✓ Combined filtering verified");
        logger.info("✓ Pagination verified");
        logger.info("✓ All assertions passed");
    }
    
    /**
     * Helper method to create a test category
     */
    private UUID createTestCategory() {
        return createTestCategory("Test Beverages", "Test category for integration testing", 1);
    }
    
    /**
     * Helper method to create a test category with custom parameters
     */
    private UUID createTestCategory(String name, String description, int displayOrder) {
        UUID categoryId = UUID.randomUUID();
        String insertSql = """
            INSERT INTO product_categories (category_id, name, description, display_order, is_active)
            VALUES (?, ?, ?, ?, ?)
            """;
        jdbcTemplate.update(insertSql, categoryId, name, description, displayOrder, true);
        return categoryId;
    }
    
    /**
     * Helper method to create multiple test products for filtering tests
     */
    private List<UUID> createMultipleTestProducts(UUID beveragesCategoryId, UUID foodCategoryId, UUID merchandiseCategoryId) {
        List<UUID> productIds = Arrays.asList(
            // Beverages (3 products)
            createTestProductInCategory("Test Espresso", "Strong coffee", SharedEnumProductType.BEVERAGE, beveragesCategoryId, "5.50", true),
            createTestProductInCategory("Test Latte", "Creamy coffee with milk", SharedEnumProductType.BEVERAGE, beveragesCategoryId, "6.00", true),
            createTestProductInCategory("Test Cappuccino", "Italian coffee specialty", SharedEnumProductType.BEVERAGE, beveragesCategoryId, "5.75", true),
            
            // Food (2 products)
            createTestProductInCategory("Test Croissant", "Buttery pastry", SharedEnumProductType.FOOD, foodCategoryId, "3.50", true),
            createTestProductInCategory("Test Muffin", "Sweet baked good", SharedEnumProductType.FOOD, foodCategoryId, "4.00", true),
            
            // Merchandise (2 products, 1 inactive)
            createTestProductInCategory("Test Mug", "Coffee mug", SharedEnumProductType.MERCHANDISE, merchandiseCategoryId, "12.99", true),
            createTestProductInCategory("Test T-Shirt", "Branded t-shirt", SharedEnumProductType.MERCHANDISE, merchandiseCategoryId, "19.99", false) // inactive
        );
        
        return productIds;
    }
    
    /**
     * Helper method to create a test product in specific category
     */
    private UUID createTestProductInCategory(String name, String description, SharedEnumProductType productType, 
                                           UUID categoryId, String price, boolean isActive) {
        UUID productId = UUID.randomUUID();
        String insertSql = """
            INSERT INTO products (product_id, name, description, product_type, category_id, 
                                base_price_amount, base_price_currency, is_active, created_at, last_modified)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """;
        jdbcTemplate.update(insertSql, productId, name, description, productType.toString(), 
                categoryId, new BigDecimal(price), "USD", isActive);
        
        // Create a basic variant for each product
        createTestVariantForProduct(productId, "Regular", "0.00");
        
        return productId;
    }
    
    /**
     * Helper method to create a test variant for a product
     */
    private void createTestVariantForProduct(UUID productId, String sizeName, String priceModifier) {
        UUID variantId = UUID.randomUUID();
        String insertSql = """
            INSERT INTO product_variants (variant_id, product_id, size_name, volume_in_oz, volume_in_ml,
                                        size_abbreviation, price_modifier_amount, price_modifier_currency, is_available)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        jdbcTemplate.update(insertSql, variantId, productId, sizeName, new BigDecimal("12.0"), 
                new BigDecimal("355.0"), "R", new BigDecimal(priceModifier), "USD", true);
    }
    
    /**
     * Helper method to create a product creation request with variants
     */
    private ApplicationDTOCreateProduct createProductRequest(UUID categoryId) {
        // Create base price
        SharedValueMoney basePrice = new SharedValueMoney(new BigDecimal("4.50"), "USD");
        
        // Create nutritional info
        SharedValueNutritionalInfo nutritionalInfo = new SharedValueNutritionalInfo(
                150, // calories
                new BigDecimal("2.5"), // totalFat
                new BigDecimal("95.0"), // caffeine
                Arrays.asList("milk", "nuts") // allergens
        );
        
        // Create variants
        ApplicationDTOCreateVariant smallVariant = new ApplicationDTOCreateVariant(
                "Small", // sizeName
                new BigDecimal("12.0"), // volumeInOz
                new BigDecimal("355.0"), // volumeInMl 
                "S", // sizeAbbreviation
                new SharedValueMoney(new BigDecimal("0.00"), "USD"), // no price modifier
                nutritionalInfo
        );
        
        ApplicationDTOCreateVariant largeVariant = new ApplicationDTOCreateVariant(
                "Large", // sizeName
                new BigDecimal("16.0"), // volumeInOz
                new BigDecimal("473.0"), // volumeInMl
                "L", // sizeAbbreviation
                new SharedValueMoney(new BigDecimal("1.50"), "USD"), // $1.50 price increase
                new SharedValueNutritionalInfo(
                        200, // higher calories for large
                        new BigDecimal("3.5"), // higher fat
                        new BigDecimal("120.0"), // higher caffeine
                        Arrays.asList("milk", "nuts")
                )
        );
        
        // Create product request
        return new ApplicationDTOCreateProduct(
                "Test Cappuccino", // name
                "Delicious test cappuccino for integration testing", // description
                SharedEnumProductType.BEVERAGE, // productType
                categoryId, // categoryId
                basePrice, // basePrice
                nutritionalInfo, // nutritionalInfo
                Arrays.asList(smallVariant, largeVariant) // variants
        );
    }
}