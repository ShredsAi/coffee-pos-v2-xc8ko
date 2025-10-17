package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityAvailability;
import ai.shreds.product_management_availability_shred_g5ds.domain.services.DomainServiceAvailabilityManagement;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueAvailabilityId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumUnavailableReason;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Domain input port for availability management operations.
 * Acts as a facade for availability-related domain services and provides
 * a clean interface for the application layer.
 */
public class DomainInputPortAvailabilityManagement {
    private final DomainServiceAvailabilityManagement availabilityService;

    /**
     * Constructs the AvailabilityManagement domain input port.
     * @param availabilityService the availability domain service
     */
    public DomainInputPortAvailabilityManagement(DomainServiceAvailabilityManagement availabilityService) {
        if (availabilityService == null) {
            throw new IllegalArgumentException("AvailabilityService cannot be null");
        }
        
        this.availabilityService = availabilityService;
    }

    /**
     * Gets availability records for a specific location.
     * @param locationId the location identifier
     * @return list of availability records for the location
     * @throws IllegalArgumentException if locationId is null
     */
    public List<DomainEntityAvailability> getAvailabilityByLocation(DomainValueLocationId locationId) {
        return availabilityService.getAvailabilityByLocation(locationId);
    }

    /**
     * Updates availability status and quantity with validation and event publishing.
     * @param availabilityId the availability identifier
     * @param isAvailable the new availability status
     * @param estimatedQuantity the new estimated quantity
     * @return the updated availability entity
     * @throws IllegalArgumentException if availability not found or validation fails
     */
    public DomainEntityAvailability updateAvailability(DomainValueAvailabilityId availabilityId, 
                                                       Boolean isAvailable, Integer estimatedQuantity) {
        if (availabilityId == null) {
            throw new IllegalArgumentException("AvailabilityId cannot be null");
        }
        
        // This method would typically get the availability from a repository first
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Availability retrieval for update not implemented - requires repository integration");
    }

    /**
     * Processes inventory updates from external systems.
     * @param locationId the location identifier
     * @param inventoryData map of product IDs to quantities
     * @throws IllegalArgumentException if parameters are invalid
     */
    public void processInventoryUpdate(DomainValueLocationId locationId, 
                                      Map<DomainValueProductId, Integer> inventoryData) {
        availabilityService.processInventoryUpdate(locationId, inventoryData);
    }

    /**
     * Checks product availability at a specific location.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @return the availability record or null if not found
     * @throws IllegalArgumentException if parameters are null
     */
    public DomainEntityAvailability checkProductAvailability(DomainValueProductId productId, 
                                                             DomainValueLocationId locationId) {
        return availabilityService.getProductAvailabilityAtLocation(productId, locationId);
    }

    /**
     * Creates a new availability record for a product at a location.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @return the created availability entity
     * @throws IllegalArgumentException if parameters are null
     */
    public DomainEntityAvailability createAvailabilityRecord(DomainValueProductId productId, 
                                                             DomainValueLocationId locationId) {
        return availabilityService.createAvailabilityRecord(productId, locationId);
    }

    /**
     * Sets a product as unavailable with a specific reason and estimated restock time.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @param reason the unavailable reason
     * @param estimatedRestockTime the estimated restock time (optional)
     * @return the updated availability entity
     * @throws IllegalArgumentException if required parameters are null
     */
    public DomainEntityAvailability setProductUnavailable(DomainValueProductId productId, 
                                                          DomainValueLocationId locationId,
                                                          SharedEnumUnavailableReason reason,
                                                          LocalDateTime estimatedRestockTime) {
        return availabilityService.setProductUnavailable(productId, locationId, reason, estimatedRestockTime);
    }

    /**
     * Sets a product as available at a location.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @param estimatedQuantity the estimated quantity
     * @return the updated availability entity
     * @throws IllegalArgumentException if parameters are null or invalid
     */
    public DomainEntityAvailability setProductAvailable(DomainValueProductId productId, 
                                                        DomainValueLocationId locationId,
                                                        Integer estimatedQuantity) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        
        // Get or create availability record
        DomainEntityAvailability availability = availabilityService.getProductAvailabilityAtLocation(productId, locationId);
        if (availability == null) {
            availability = availabilityService.createAvailabilityRecord(productId, locationId);
        }
        
        // Update to available status
        return availabilityService.updateAvailability(availability, true, estimatedQuantity);
    }

    /**
     * Updates the quantity for a product at a location.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @param newQuantity the new quantity
     * @return the updated availability entity
     * @throws IllegalArgumentException if parameters are null or invalid
     */
    public DomainEntityAvailability updateProductQuantity(DomainValueProductId productId, 
                                                          DomainValueLocationId locationId,
                                                          Integer newQuantity) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        
        // Get or create availability record
        DomainEntityAvailability availability = availabilityService.getProductAvailabilityAtLocation(productId, locationId);
        if (availability == null) {
            availability = availabilityService.createAvailabilityRecord(productId, locationId);
        }
        
        // This would need to call updateQuantity on the availability entity
        // For now, we'll use the general update method
        return availabilityService.updateAvailability(availability, availability.getIsAvailable(), newQuantity);
    }

    /**
     * Validates that a quantity is non-negative.
     * @param quantity the quantity to validate
     * @throws IllegalArgumentException if quantity is negative
     */
    public void validateNonNegativeQuantity(Integer quantity) {
        availabilityService.validateNonNegativeQuantity(quantity);
    }

    /**
     * Validates that an unavailable reason is required when status is unavailable.
     * @param isAvailable the availability status
     * @param reason the unavailable reason
     * @throws IllegalArgumentException if validation fails
     */
    public void validateUnavailableReasonRequired(Boolean isAvailable, SharedEnumUnavailableReason reason) {
        availabilityService.validateUnavailableReasonRequired(isAvailable, reason);
    }

    /**
     * Invalidates cache for a specific product at a location.
     * @param productId the product identifier
     * @param locationId the location identifier
     */
    public void invalidateAvailabilityCache(DomainValueProductId productId, DomainValueLocationId locationId) {
        availabilityService.invalidateAvailabilityCache(productId, locationId);
    }

    /**
     * Invalidates all availability cache entries.
     */
    public void invalidateAllAvailabilityCache() {
        availabilityService.invalidateAllAvailabilityCache();
    }

    /**
     * Checks if a product is available at a specific location.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @return true if product is available, false otherwise
     */
    public boolean isProductAvailableAtLocation(DomainValueProductId productId, DomainValueLocationId locationId) {
        DomainEntityAvailability availability = availabilityService.getProductAvailabilityAtLocation(productId, locationId);
        return availability != null && availability.getIsAvailable();
    }

    /**
     * Gets the estimated quantity for a product at a location.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @return the estimated quantity or 0 if not available
     */
    public Integer getEstimatedQuantityAtLocation(DomainValueProductId productId, DomainValueLocationId locationId) {
        DomainEntityAvailability availability = availabilityService.getProductAvailabilityAtLocation(productId, locationId);
        return availability != null ? availability.getEstimatedQuantity() : 0;
    }

    /**
     * Gets the unavailable reason for a product at a location.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @return the unavailable reason or null if available
     */
    public SharedEnumUnavailableReason getUnavailableReason(DomainValueProductId productId, 
                                                           DomainValueLocationId locationId) {
        DomainEntityAvailability availability = availabilityService.getProductAvailabilityAtLocation(productId, locationId);
        return availability != null ? availability.getUnavailableReason() : null;
    }

    /**
     * Gets the estimated restock time for a product at a location.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @return the estimated restock time or null if available or no estimate
     */
    public LocalDateTime getEstimatedRestockTime(DomainValueProductId productId, DomainValueLocationId locationId) {
        DomainEntityAvailability availability = availabilityService.getProductAvailabilityAtLocation(productId, locationId);
        return availability != null ? availability.getEstimatedRestockTime() : null;
    }
}