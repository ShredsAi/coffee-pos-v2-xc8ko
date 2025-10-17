package ai.shreds.product_management_availability_shred_g5ds.application;

import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumUnavailableReason;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ApplicationDTOAvailabilityUpdate {
    
    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;
    
    @Min(value = 0, message = "Estimated quantity must be non-negative")
    private Integer estimatedQuantity;
    
    private SharedEnumUnavailableReason unavailableReason;
    
    private LocalDateTime estimatedRestockTime;

    // Default constructor
    public ApplicationDTOAvailabilityUpdate() {}

    // Constructor with required fields
    public ApplicationDTOAvailabilityUpdate(Boolean isAvailable, Integer estimatedQuantity) {
        this.isAvailable = isAvailable;
        this.estimatedQuantity = estimatedQuantity;
    }

    // Full constructor
    public ApplicationDTOAvailabilityUpdate(Boolean isAvailable, Integer estimatedQuantity,
                                           SharedEnumUnavailableReason unavailableReason,
                                           LocalDateTime estimatedRestockTime) {
        this.isAvailable = isAvailable;
        this.estimatedQuantity = estimatedQuantity;
        this.unavailableReason = unavailableReason;
        this.estimatedRestockTime = estimatedRestockTime;
    }

    // Getters and Setters
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getEstimatedQuantity() {
        return estimatedQuantity;
    }

    public void setEstimatedQuantity(Integer estimatedQuantity) {
        this.estimatedQuantity = estimatedQuantity;
    }

    public SharedEnumUnavailableReason getUnavailableReason() {
        return unavailableReason;
    }

    public void setUnavailableReason(SharedEnumUnavailableReason unavailableReason) {
        this.unavailableReason = unavailableReason;
    }

    public LocalDateTime getEstimatedRestockTime() {
        return estimatedRestockTime;
    }

    public void setEstimatedRestockTime(LocalDateTime estimatedRestockTime) {
        this.estimatedRestockTime = estimatedRestockTime;
    }
}