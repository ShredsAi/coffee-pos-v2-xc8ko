package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityAvailability;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;

/**
 * Domain output port for cache management operations.
 * Defines the contract for caching availability data to improve performance.
 */
public interface DomainOutputPortCacheManager {
    
    /**
     * Caches an availability entity for fast retrieval.
     * @param availability the availability entity to cache
     */
    void cacheAvailability(DomainEntityAvailability availability);
    
    /**
     * Retrieves cached availability data for a product at a location.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @return the cached availability entity or null if not found in cache
     */
    DomainEntityAvailability getCachedAvailability(DomainValueProductId productId, DomainValueLocationId locationId);
    
    /**
     * Evicts availability cache entry for a specific product at a location.
     * @param productId the product identifier
     * @param locationId the location identifier
     */
    void evictAvailabilityCache(DomainValueProductId productId, DomainValueLocationId locationId);
    
    /**
     * Evicts all availability cache entries.
     * This is typically used for cache invalidation during maintenance or errors.
     */
    void evictAllAvailabilityCache();
}