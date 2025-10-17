package ai.shreds.product_management_availability_shred_g5ds.configurations;

import ai.shreds.product_management_availability_shred_g5ds.domain.services.*;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Domain layer configuration for wiring domain services and input ports.
 * This configuration follows hexagonal architecture principles by injecting
 * infrastructure implementations through constructor dependency injection.
 */
@Configuration
public class DomainConfiguration {

    /**
     * Creates the domain service for product management operations.
     * 
     * @param productRepository repository for product persistence
     * @param categoryRepository repository for category persistence
     * @param eventPublisher output port for publishing domain events
     * @return DomainServiceProductManagement instance
     */
    @Bean
    public DomainServiceProductManagement domainServiceProductManagement(
            DomainOutputPortProductRepository productRepository,
            DomainOutputPortCategoryRepository categoryRepository,
            DomainOutputPortEventPublisher eventPublisher) {
        return new DomainServiceProductManagement(productRepository, categoryRepository, eventPublisher);
    }

    /**
     * Creates the domain service for category management operations.
     * 
     * @param categoryRepository repository for category persistence
     * @return DomainServiceCategoryManagement instance
     */
    @Bean
    public DomainServiceCategoryManagement domainServiceCategoryManagement(
            DomainOutputPortCategoryRepository categoryRepository) {
        return new DomainServiceCategoryManagement(categoryRepository);
    }

    /**
     * Creates the domain service for variant management operations.
     * 
     * @param variantRepository repository for variant persistence
     * @param productRepository repository for product persistence
     * @return DomainServiceVariantManagement instance
     */
    @Bean
    public DomainServiceVariantManagement domainServiceVariantManagement(
            DomainOutputPortVariantRepository variantRepository,
            DomainOutputPortProductRepository productRepository) {
        return new DomainServiceVariantManagement(variantRepository, productRepository);
    }

    /**
     * Creates the domain service for availability management operations.
     * 
     * @param availabilityRepository repository for availability persistence
     * @param cacheManager cache manager for availability caching
     * @param eventPublisher output port for publishing domain events
     * @return DomainServiceAvailabilityManagement instance
     */
    @Bean
    public DomainServiceAvailabilityManagement domainServiceAvailabilityManagement(
            DomainOutputPortAvailabilityRepository availabilityRepository,
            DomainOutputPortCacheManager cacheManager,
            DomainOutputPortEventPublisher eventPublisher) {
        return new DomainServiceAvailabilityManagement(availabilityRepository, cacheManager, eventPublisher);
    }

    /**
     * Creates the domain service for inventory synchronization operations.
     * 
     * @param inventoryClient client for external inventory service
     * @param availabilityService domain service for availability management
     * @return DomainServiceInventorySync instance
     */
    @Bean
    public DomainServiceInventorySync domainServiceInventorySync(
            DomainOutputPortInventoryClient inventoryClient,
            DomainServiceAvailabilityManagement availabilityService) {
        return new DomainServiceInventorySync(inventoryClient, availabilityService);
    }

    /**
     * Creates the input port for product management use cases.
     * 
     * @param productService domain service for product operations
     * @return DomainInputPortProductManagement instance
     */
    @Bean
    public DomainInputPortProductManagement domainInputPortProductManagement(
            DomainServiceProductManagement productService) {
        return new DomainInputPortProductManagement(productService);
    }

    /**
     * Creates the input port for category management use cases.
     * 
     * @param categoryService domain service for category operations
     * @return DomainInputPortCategoryManagement instance
     */
    @Bean
    public DomainInputPortCategoryManagement domainInputPortCategoryManagement(
            DomainServiceCategoryManagement categoryService) {
        return new DomainInputPortCategoryManagement(categoryService);
    }

    /**
     * Creates the input port for variant management use cases.
     * 
     * @param variantService domain service for variant operations
     * @return DomainInputPortVariantManagement instance
     */
    @Bean
    public DomainInputPortVariantManagement domainInputPortVariantManagement(
            DomainServiceVariantManagement variantService) {
        return new DomainInputPortVariantManagement(variantService);
    }

    /**
     * Creates the input port for availability management use cases.
     * 
     * @param availabilityService domain service for availability operations
     * @return DomainInputPortAvailabilityManagement instance
     */
    @Bean
    public DomainInputPortAvailabilityManagement domainInputPortAvailabilityManagement(
            DomainServiceAvailabilityManagement availabilityService) {
        return new DomainInputPortAvailabilityManagement(availabilityService);
    }

    /**
     * Creates the input port for inventory synchronization use cases.
     * 
     * @param inventorySyncService domain service for inventory sync operations
     * @return DomainInputPortInventorySync instance
     */
    @Bean
    public DomainInputPortInventorySync domainInputPortInventorySync(
            DomainServiceInventorySync inventorySyncService) {
        return new DomainInputPortInventorySync(inventorySyncService);
    }
}