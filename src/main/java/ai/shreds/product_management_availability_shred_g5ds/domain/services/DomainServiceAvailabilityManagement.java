package ai.shreds.product_management_availability_shred_g5ds.domain.services;

import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventAvailabilityChanged;
import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityAvailability;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueAvailabilityId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortAvailabilityRepository;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortCacheManager;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortEventPublisher;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumUnavailableReason;

import java.util.List;
import java.util.Map;

/**
 * Domain service responsible for availability management operations.
 * Handles availability updates, cache management, and event publishing.
 */
public class DomainServiceAvailabilityManagement {
    private final DomainOutputPortAvailabilityRepository availabilityRepository;
    private final DomainOutputPortCacheManager cacheManager;
    private final DomainOutputPortEventPublisher eventPublisher;

    /**
     * Constructs the AvailabilityManagement domain service.
     * @param availabilityRepository the availability repository port
     * @param cacheManager the cache manager port
     * @param eventPublisher the event publisher port
     */
    public DomainServiceAvailabilityManagement(DomainOutputPortAvailabilityRepository availabilityRepository,
                                              DomainOutputPortCacheManager cacheManager,
                                              DomainOutputPortEventPublisher eventPublisher) {
        if (availabilityRepository == null) {
            throw new IllegalArgumentException("AvailabilityRepository cannot be null");
        }
        if (cacheManager == null) {
            throw new IllegalArgumentException("CacheManager cannot be null");
        }
        if (eventPublisher == null) {
            throw new IllegalArgumentException("EventPublisher cannot be null");
        }
        
        this.availabilityRepository = availabilityRepository;
        this.cacheManager = cacheManager;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Updates availability status and quantity with validation and event publishing.
     * @param availability the availability entity to update
     * @param isAvailable the new availability status
     * @param estimatedQuantity the new estimated quantity
     * @return the updated availability entity
     * @throws IllegalArgumentException if validation fails
     */
    public DomainEntityAvailability updateAvailability(DomainEntityAvailability availability, 
                                                       Boolean isAvailable, Integer estimatedQuantity) {
        if (availability == null) {
            throw new IllegalArgumentException("Availability cannot be null");
        }
        
        // Validate input parameters
        validateNonNegativeQuantity(estimatedQuantity);
        validateUnavailableReasonRequired(isAvailable, null);
        
        // Update availability and get potential event
        DomainEventAvailabilityChanged event = availability.updateAvailabilityStatus(isAvailable, estimatedQuantity);
        
        // Validate business rules
        availability.validateBusinessRules();
        
        // Save updated availability
        availability = availabilityRepository.save(availability);
        
        // Update cache
        cacheManager.cacheAvailability(availability);
        
        // Publish event if there was a change
        if (event != null) {
            eventPublisher.publishAvailabilityChangedEvent(event);
        }
        
        return availability;
    }

    /**
     * Gets availability records for a specific location with cache optimization.
     * @param locationId the location identifier
     * @return list of availability records for the location
     */
    public List<DomainEntityAvailability> getAvailabilityByLocation(DomainValueLocationId locationId) {
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        
        // Get from repository (cache is handled at individual product level)
        return availabilityRepository.findByLocationId(locationId);
    }

    /**
     * Gets availability for a specific product at a specific location with cache optimization.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @return the availability record or null if not found
     */
    public DomainEntityAvailability getProductAvailabilityAtLocation(DomainValueProductId productId, 
                                                                     DomainValueLocationId locationId) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        
        // Try cache first
        DomainEntityAvailability cached = cacheManager.getCachedAvailability(productId, locationId);
        if (cached != null) {
            return cached;
        }
        
        // Fall back to repository
        DomainEntityAvailability availability = availabilityRepository.findByProductAndLocation(productId, locationId);
        
        // Cache the result if found
        if (availability != null) {
            cacheManager.cacheAvailability(availability);
        }
        
        return availability;
    }

    /**
     * Processes inventory updates from external systems.
     * @param locationId the location identifier
     * @param inventoryData map of product IDs to quantities
     */
    public void processInventoryUpdate(DomainValueLocationId locationId, 
                                      Map<DomainValueProductId, Integer> inventoryData) {
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        if (inventoryData == null) {
            throw new IllegalArgumentException("InventoryData cannot be null");
        }
        
        for (Map.Entry<DomainValueProductId, Integer> entry : inventoryData.entrySet()) {
            DomainValueProductId productId = entry.getKey();
            Integer quantity = entry.getValue();
            
            try {
                // Validate quantity
                validateNonNegativeQuantity(quantity);
                
                // Find or create availability record
                DomainEntityAvailability availability = availabilityRepository.findByProductAndLocation(productId, locationId);
                if (availability == null) {
                    // Create new availability record
                    availability = new DomainEntityAvailability(productId, locationId);
                }
                
                // Update quantity and get potential event
                DomainEventAvailabilityChanged event = availability.updateQuantity(quantity);
                
                // Validate business rules
                availability.validateBusinessRules();
                
                // Save updated availability
                availability = availabilityRepository.save(availability);
                
                // Update cache
                cacheManager.cacheAvailability(availability);
                
                // Publish event if there was a change
                if (event != null) {
                    eventPublisher.publishAvailabilityChangedEvent(event);
                }
                
            } catch (Exception e) {
                // Log error but continue processing other products
                // In a real implementation, this would use proper logging
                System.err.println(String.format("Error processing inventory update for product %s at location %s: %s", 
                        productId, locationId, e.getMessage()));
            }
        }
    }

    /**
     * Sets a product as unavailable with a specific reason and estimated restock time.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @param reason the unavailable reason
     * @param estimatedRestockTime the estimated restock time (optional)
     * @return the updated availability entity
     */
    public DomainEntityAvailability setProductUnavailable(DomainValueProductId productId, 
                                                          DomainValueLocationId locationId,
                                                          SharedEnumUnavailableReason reason,
                                                          java.time.LocalDateTime estimatedRestockTime) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        if (reason == null) {
            throw new IllegalArgumentException("Unavailable reason cannot be null");
        }
        
        // Find or create availability record
        DomainEntityAvailability availability = availabilityRepository.findByProductAndLocation(productId, locationId);
        if (availability == null) {
            availability = new DomainEntityAvailability(productId, locationId);
        }
        
        // Set unavailable with reason
        DomainEventAvailabilityChanged event = availability.setUnavailable(reason, estimatedRestockTime);
        
        // Validate business rules
        availability.validateBusinessRules();
        
        // Save updated availability
        availability = availabilityRepository.save(availability);
        
        // Update cache
        cacheManager.cacheAvailability(availability);
        
        // Publish event if there was a change
        if (event != null) {
            eventPublisher.publishAvailabilityChangedEvent(event);
        }
        
        return availability;
    }

    /**
     * Validates that a quantity is non-negative.
     * @param quantity the quantity to validate
     * @throws IllegalArgumentException if quantity is negative
     */
    public void validateNonNegativeQuantity(Integer quantity) {
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative: " + quantity);
        }
    }

    /**
     * Validates that an unavailable reason is required when status is unavailable.
     * @param isAvailable the availability status
     * @param reason the unavailable reason
     * @throws IllegalArgumentException if validation fails
     */
    public void validateUnavailableReasonRequired(Boolean isAvailable, SharedEnumUnavailableReason reason) {
        if (isAvailable != null && !isAvailable && reason == null) {
            throw new IllegalArgumentException("Unavailable reason is required when product is not available");
        }
    }

    /**
     * Creates a new availability record for a product at a location.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @return the created availability entity
     */
    public DomainEntityAvailability createAvailabilityRecord(DomainValueProductId productId, 
                                                             DomainValueLocationId locationId) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        
        // Check if record already exists
        DomainEntityAvailability existing = availabilityRepository.findByProductAndLocation(productId, locationId);
        if (existing != null) {
            return existing; // Return existing record
        }
        
        // Create new availability record
        DomainEntityAvailability availability = new DomainEntityAvailability(productId, locationId);
        
        // Validate business rules
        availability.validateBusinessRules();
        
        // Save availability
        availability = availabilityRepository.save(availability);
        
        // Cache the new record
        cacheManager.cacheAvailability(availability);
        
        return availability;
    }

    /**
     * Invalidates cache for a specific product at a location.
     * @param productId the product identifier
     * @param locationId the location identifier
     */
    public void invalidateAvailabilityCache(DomainValueProductId productId, DomainValueLocationId locationId) {
        cacheManager.evictAvailabilityCache(productId, locationId);
    }

    /**
     * Invalidates all availability cache entries.
     */
    public void invalidateAllAvailabilityCache() {
        cacheManager.evictAllAvailabilityCache();
    }
}