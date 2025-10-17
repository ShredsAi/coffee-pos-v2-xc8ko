package ai.shreds.product_management_availability_shred_g5ds.domain.entities;

import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventAvailabilityChanged;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueAvailabilityId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumUnavailableReason;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing product availability at a specific location.
 * Tracks availability status, quantities, and reasons for unavailability.
 */
public class DomainEntityAvailability {
    private DomainValueAvailabilityId availabilityId;
    private DomainValueProductId productId;
    private DomainValueLocationId locationId;
    private Boolean isAvailable;
    private Integer estimatedQuantity;
    private SharedEnumUnavailableReason unavailableReason;
    private LocalDateTime lastUpdated;
    private LocalDateTime estimatedRestockTime;

    /**
     * Constructs a new Availability entity.
     * @param productId the product identifier
     * @param locationId the location identifier
     */
    public DomainEntityAvailability(DomainValueProductId productId, DomainValueLocationId locationId) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        
        this.availabilityId = DomainValueAvailabilityId.generate();
        this.productId = productId;
        this.locationId = locationId;
        this.isAvailable = true;
        this.estimatedQuantity = 0;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Updates the availability status and quantity.
     * @param isAvailable the new availability status
     * @param estimatedQuantity the new estimated quantity
     * @return domain event representing the availability change
     */
    public DomainEventAvailabilityChanged updateAvailabilityStatus(Boolean isAvailable, Integer estimatedQuantity) {
        if (isAvailable == null) {
            throw new IllegalArgumentException("Availability status cannot be null");
        }
        if (estimatedQuantity != null && estimatedQuantity < 0) {
            throw new IllegalArgumentException("Estimated quantity cannot be negative");
        }
        
        boolean hasChanged = hasAvailabilityChanged(isAvailable, estimatedQuantity);
        
        this.isAvailable = isAvailable;
        this.estimatedQuantity = estimatedQuantity != null ? estimatedQuantity : 0;
        this.lastUpdated = LocalDateTime.now();
        
        // Clear unavailable reason if becoming available
        if (isAvailable) {
            this.unavailableReason = null;
            this.estimatedRestockTime = null;
        }
        
        if (hasChanged) {
            return new DomainEventAvailabilityChanged(productId, locationId, isAvailable, this.estimatedQuantity);
        }
        
        return null;
    }

    /**
     * Sets the product as unavailable with a reason and estimated restock time.
     * @param reason the reason for unavailability
     * @param estimatedRestockTime the estimated restock time
     * @return domain event representing the availability change
     */
    public DomainEventAvailabilityChanged setUnavailable(SharedEnumUnavailableReason reason, 
                                                        LocalDateTime estimatedRestockTime) {
        if (reason == null) {
            throw new IllegalArgumentException("Unavailable reason cannot be null when setting unavailable");
        }
        
        boolean hasChanged = this.isAvailable || !Objects.equals(this.unavailableReason, reason);
        
        this.isAvailable = false;
        this.estimatedQuantity = 0;
        this.unavailableReason = reason;
        this.estimatedRestockTime = estimatedRestockTime;
        this.lastUpdated = LocalDateTime.now();
        
        if (hasChanged) {
            return new DomainEventAvailabilityChanged(productId, locationId, false, 0, reason, estimatedRestockTime);
        }
        
        return null;
    }

    /**
     * Updates the quantity without changing availability status.
     * @param newQuantity the new quantity
     * @return domain event representing the availability change
     */
    public DomainEventAvailabilityChanged updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        
        boolean hasChanged = !Objects.equals(this.estimatedQuantity, newQuantity);
        
        this.estimatedQuantity = newQuantity;
        this.lastUpdated = LocalDateTime.now();
        
        // If quantity is 0 and was available, mark as out of stock
        if (newQuantity == 0 && this.isAvailable) {
            this.isAvailable = false;
            this.unavailableReason = SharedEnumUnavailableReason.OUT_OF_STOCK;
            hasChanged = true;
        }
        // If quantity > 0 and was unavailable due to stock, mark as available
        else if (newQuantity > 0 && !this.isAvailable && 
                this.unavailableReason == SharedEnumUnavailableReason.OUT_OF_STOCK) {
            this.isAvailable = true;
            this.unavailableReason = null;
            this.estimatedRestockTime = null;
            hasChanged = true;
        }
        
        if (hasChanged) {
            return new DomainEventAvailabilityChanged(productId, locationId, this.isAvailable, newQuantity);
        }
        
        return null;
    }

    /**
     * Validates business rules for availability.
     * @throws IllegalStateException if business rules are violated
     */
    public void validateBusinessRules() {
        if (estimatedQuantity < 0) {
            throw new IllegalStateException("Estimated quantity cannot be negative");
        }
        if (!isAvailable && unavailableReason == null) {
            throw new IllegalStateException("Unavailable reason is required when product is not available");
        }
        if (isAvailable && unavailableReason != null) {
            throw new IllegalStateException("Unavailable reason should be null when product is available");
        }
    }

    /**
     * Checks if availability has changed compared to new values.
     * @param newStatus the new availability status
     * @param newQuantity the new quantity
     * @return true if availability has changed
     */
    public boolean hasAvailabilityChanged(Boolean newStatus, Integer newQuantity) {
        return !Objects.equals(this.isAvailable, newStatus) || 
               !Objects.equals(this.estimatedQuantity, newQuantity);
    }

    // Getters
    public DomainValueAvailabilityId getId() {
        return availabilityId;
    }

    public DomainValueProductId getProductId() {
        return productId;
    }

    public DomainValueLocationId getLocationId() {
        return locationId;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public Integer getEstimatedQuantity() {
        return estimatedQuantity;
    }

    public SharedEnumUnavailableReason getUnavailableReason() {
        return unavailableReason;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public LocalDateTime getEstimatedRestockTime() {
        return estimatedRestockTime;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainEntityAvailability that = (DomainEntityAvailability) other;
        return Objects.equals(availabilityId, that.availabilityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(availabilityId);
    }

    @Override
    public String toString() {
        return String.format("DomainEntityAvailability{availabilityId=%s, productId=%s, locationId=%s, isAvailable=%s, estimatedQuantity=%d}",
            availabilityId, productId, locationId, isAvailable, estimatedQuantity);
    }
}