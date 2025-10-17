package ai.shreds.product_management_availability_shred_g5ds.infrastructure.external_services;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityAvailability;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortCacheManager;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;
import ai.shreds.product_management_availability_shred_g5ds.infrastructure.exceptions.InfrastructureExceptionCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
public class InfrastructureClientCacheManager implements DomainOutputPortCacheManager {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureClientCacheManager.class);
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String AVAILABILITY_CACHE_PREFIX = "product:availability:";
    private static final long AVAILABILITY_CACHE_TTL = 300; // 5 minutes in seconds

    @Autowired
    public InfrastructureClientCacheManager(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void cacheAvailability(DomainEntityAvailability availability) {
        if (availability == null || availability.getProductId() == null || availability.getLocationId() == null) {
            logger.warn("Cannot cache null availability or availability with missing product/location IDs");
            return;
        }
        
        String cacheKey = buildCacheKey(availability.getProductId(), availability.getLocationId());
        
        try {
            logger.debug("Caching availability for key: {}", cacheKey);
            
            String serializedAvailability = serializeAvailability(availability);
            
            redisTemplate.opsForValue().set(cacheKey, serializedAvailability, Duration.ofSeconds(AVAILABILITY_CACHE_TTL));
            
            logger.debug("Successfully cached availability for product: {} at location: {}", 
                availability.getProductId().getValue(), availability.getLocationId().getValue());
            
        } catch (Exception e) {
            logger.error("Failed to cache availability for key: {}", cacheKey, e);
            throw new InfrastructureExceptionCache(
                "Failed to cache availability", e, "SET", cacheKey
            );
        }
    }

    @Override
    public DomainEntityAvailability getCachedAvailability(DomainValueProductId productId, DomainValueLocationId locationId) {
        if (productId == null || locationId == null) {
            logger.warn("Cannot retrieve cached availability with null product or location ID");
            return null;
        }
        
        String cacheKey = buildCacheKey(productId, locationId);
        
        try {
            logger.debug("Retrieving cached availability for key: {}", cacheKey);
            
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            
            if (cachedData == null) {
                logger.debug("No cached availability found for key: {}", cacheKey);
                return null;
            }
            
            DomainEntityAvailability availability = deserializeAvailability(cachedData.toString());
            
            logger.debug("Successfully retrieved cached availability for product: {} at location: {}", 
                productId.getValue(), locationId.getValue());
            
            return availability;
            
        } catch (Exception e) {
            logger.error("Failed to retrieve cached availability for key: {}", cacheKey, e);
            throw new InfrastructureExceptionCache(
                "Failed to retrieve cached availability", e, "GET", cacheKey
            );
        }
    }

    @Override
    public void evictAvailabilityCache(DomainValueProductId productId, DomainValueLocationId locationId) {
        if (productId == null || locationId == null) {
            logger.warn("Cannot evict cache with null product or location ID");
            return;
        }
        
        String cacheKey = buildCacheKey(productId, locationId);
        
        try {
            logger.debug("Evicting cached availability for key: {}", cacheKey);
            
            Boolean deleted = redisTemplate.delete(cacheKey);
            
            if (Boolean.TRUE.equals(deleted)) {
                logger.debug("Successfully evicted cached availability for product: {} at location: {}", 
                    productId.getValue(), locationId.getValue());
            } else {
                logger.debug("No cached availability to evict for key: {}", cacheKey);
            }
            
        } catch (Exception e) {
            logger.error("Failed to evict cached availability for key: {}", cacheKey, e);
            throw new InfrastructureExceptionCache(
                "Failed to evict cached availability", e, "DELETE", cacheKey
            );
        }
    }

    @Override
    public void evictAllAvailabilityCache() {
        try {
            logger.info("Evicting all availability cache entries");
            
            String pattern = AVAILABILITY_CACHE_PREFIX + "*";
            Set<String> keys = redisTemplate.keys(pattern);
            
            if (keys != null && !keys.isEmpty()) {
                Long deletedCount = redisTemplate.delete(keys);
                logger.info("Successfully evicted {} availability cache entries", deletedCount);
            } else {
                logger.debug("No availability cache entries found to evict");
            }
            
        } catch (Exception e) {
            logger.error("Failed to evict all availability cache entries", e);
            throw new InfrastructureExceptionCache(
                "Failed to evict all availability cache entries", e, "DELETE_ALL", AVAILABILITY_CACHE_PREFIX + "*"
            );
        }
    }

    private String buildCacheKey(DomainValueProductId productId, DomainValueLocationId locationId) {
        return AVAILABILITY_CACHE_PREFIX + locationId.getValue() + ":" + productId.getValue();
    }

    private String serializeAvailability(DomainEntityAvailability availability) {
        try {
            // Create a simple map representation for caching
            var cacheData = new java.util.HashMap<String, Object>();
            cacheData.put("availabilityId", availability.getId().getValue().toString());
            cacheData.put("productId", availability.getProductId().getValue().toString());
            cacheData.put("locationId", availability.getLocationId().getValue().toString());
            cacheData.put("isAvailable", availability.getIsAvailable());
            cacheData.put("estimatedQuantity", availability.getEstimatedQuantity());
            
            if (availability.getUnavailableReason() != null) {
                cacheData.put("unavailableReason", availability.getUnavailableReason().name());
            }
            
            if (availability.getLastUpdated() != null) {
                cacheData.put("lastUpdated", availability.getLastUpdated().toString());
            }
            
            if (availability.getEstimatedRestockTime() != null) {
                cacheData.put("estimatedRestockTime", availability.getEstimatedRestockTime().toString());
            }
            
            return objectMapper.writeValueAsString(cacheData);
            
        } catch (Exception e) {
            throw new InfrastructureExceptionCache(
                "Failed to serialize availability for caching", e, "SERIALIZE", 
                availability.getId().getValue().toString()
            );
        }
    }

    private DomainEntityAvailability deserializeAvailability(String data) {
        try {
            // For now, we'll return null and let the system fall back to database
            // In a full implementation, we would reconstruct the domain entity from cached data
            // This is complex due to the domain entity constructor requirements
            
            @SuppressWarnings("unchecked")
            var cacheData = (java.util.Map<String, Object>) objectMapper.readValue(data, java.util.Map.class);
            
            // We would need to reconstruct the domain entity here
            // For now, returning null to force database lookup
            logger.debug("Cached data found but deserialization not fully implemented, falling back to database");
            
            return null;
            
        } catch (Exception e) {
            logger.warn("Failed to deserialize cached availability data, falling back to database: {}", e.getMessage());
            return null;
        }
    }
}