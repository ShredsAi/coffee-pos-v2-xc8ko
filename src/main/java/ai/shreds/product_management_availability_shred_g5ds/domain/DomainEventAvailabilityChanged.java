package ai.shreds.product_management_availability_shred_g5ds.domain;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumUnavailableReason;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain event representing a change in product availability at a specific location.
 * Immutable event that captures availability status changes with reason and timing details.
 */
public class DomainEventAvailabilityChanged {
    private final DomainValueProductId productId;
    private final DomainValueLocationId locationId;
    private final Boolean isAvailable;
    private final SharedEnumUnavailableReason unavailableReason;
    private final Integer estimatedQuantity;
    private final LocalDateTime estimatedRestockTime;
    private final LocalDateTime timestamp;

    /**
     * Constructs an AvailabilityChanged domain event.
     * @param productId the unique identifier of the product
     * @param locationId the location where availability changed
     * @param isAvailable the new availability status
     * @param estimatedQuantity the estimated quantity available
     */
    public DomainEventAvailabilityChanged(DomainValueProductId productId, DomainValueLocationId locationId, 
                                        Boolean isAvailable, Integer estimatedQuantity) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        if (isAvailable == null) {
            throw new IllegalArgumentException("IsAvailable cannot be null");
        }
        if (estimatedQuantity != null && estimatedQuantity < 0) {
            throw new IllegalArgumentException("Estimated quantity cannot be negative");
        }
        
        this.productId = productId;
        this.locationId = locationId;
        this.isAvailable = isAvailable;
        this.estimatedQuantity = estimatedQuantity;
        this.unavailableReason = null;
        this.estimatedRestockTime = null;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructs an AvailabilityChanged domain event with unavailable reason and restock time.
     * @param productId the unique identifier of the product
     * @param locationId the location where availability changed
     * @param isAvailable the new availability status
     * @param estimatedQuantity the estimated quantity available
     * @param unavailableReason the reason for unavailability
     * @param estimatedRestockTime the estimated restock time
     */
    public DomainEventAvailabilityChanged(DomainValueProductId productId, DomainValueLocationId locationId, 
                                        Boolean isAvailable, Integer estimatedQuantity,
                                        SharedEnumUnavailableReason unavailableReason, 
                                        LocalDateTime estimatedRestockTime) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        if (isAvailable == null) {
            throw new IllegalArgumentException("IsAvailable cannot be null");
        }
        if (estimatedQuantity != null && estimatedQuantity < 0) {
            throw new IllegalArgumentException("Estimated quantity cannot be negative");
        }
        
        this.productId = productId;
        this.locationId = locationId;
        this.isAvailable = isAvailable;
        this.estimatedQuantity = estimatedQuantity;
        this.unavailableReason = unavailableReason;
        this.estimatedRestockTime = estimatedRestockTime;
        this.timestamp = LocalDateTime.now();
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

    public SharedEnumUnavailableReason getUnavailableReason() {
        return unavailableReason;
    }

    public Integer getEstimatedQuantity() {
        return estimatedQuantity;
    }

    public LocalDateTime getEstimatedRestockTime() {
        return estimatedRestockTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainEventAvailabilityChanged that = (DomainEventAvailabilityChanged) other;
        return Objects.equals(productId, that.productId) &&
               Objects.equals(locationId, that.locationId) &&
               Objects.equals(isAvailable, that.isAvailable) &&
               Objects.equals(unavailableReason, that.unavailableReason) &&
               Objects.equals(estimatedQuantity, that.estimatedQuantity) &&
               Objects.equals(estimatedRestockTime, that.estimatedRestockTime) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, locationId, isAvailable, unavailableReason, 
                          estimatedQuantity, estimatedRestockTime, timestamp);
    }

    @Override
    public String toString() {
        return String.format("DomainEventAvailabilityChanged{productId=%s, locationId=%s, isAvailable=%s, unavailableReason=%s, estimatedQuantity=%d, estimatedRestockTime=%s, timestamp=%s}",
            productId, locationId, isAvailable, unavailableReason, estimatedQuantity, estimatedRestockTime, timestamp);
    }
}