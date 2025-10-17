package ai.shreds.product_management_availability_shred_g5ds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application for Product Management & Availability Shred
 * 
 * This application provides comprehensive product management capabilities including:
 * - Product lifecycle management (create, read, update, delete)
 * - Category and variant management
 * - Real-time availability tracking
 * - Integration with external inventory management service
 * - Redis caching for performance optimization
 * - JMS messaging for event-driven architecture
 * 
 * @author Product Management Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableJms
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class ProductManagementAvailabilityShredApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductManagementAvailabilityShredApplication.class, args);
    }

}