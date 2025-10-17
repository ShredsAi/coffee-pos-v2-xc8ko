package ai.shreds.product_management_availability_shred_g5ds.shared.dtos;

import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumUnavailableReason;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class SharedAvailabilityDTO {
    @NotNull
    @JsonProperty("availabilityId")
    private UUID availabilityId;

    @NotNull
    @JsonProperty("productId")
    private UUID productId;

    @NotNull
    @JsonProperty("locationId")
    private UUID locationId;

    @JsonProperty("isAvailable")
    private Boolean isAvailable = true;

    @Min(0)
    @JsonProperty("estimatedQuantity")
    private Integer estimatedQuantity = 0;

    @JsonProperty("unavailableReason")
    private SharedEnumUnavailableReason unavailableReason;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("lastUpdated")
    private LocalDateTime lastUpdated;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("estimatedRestockTime")
    private LocalDateTime estimatedRestockTime;

    public SharedAvailabilityDTO() {}

    public SharedAvailabilityDTO(UUID availabilityId, UUID productId, UUID locationId, Boolean isAvailable,
                                Integer estimatedQuantity, SharedEnumUnavailableReason unavailableReason,
                                LocalDateTime lastUpdated, LocalDateTime estimatedRestockTime) {
        this.availabilityId = availabilityId;
        this.productId = productId;
        this.locationId = locationId;
        this.isAvailable = isAvailable;
        this.estimatedQuantity = estimatedQuantity;
        this.unavailableReason = unavailableReason;
        this.lastUpdated = lastUpdated;
        this.estimatedRestockTime = estimatedRestockTime;
    }

    public UUID getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(UUID availabilityId) {
        this.availabilityId = availabilityId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getEstimatedRestockTime() {
        return estimatedRestockTime;
    }

    public void setEstimatedRestockTime(LocalDateTime estimatedRestockTime) {
        this.estimatedRestockTime = estimatedRestockTime;
    }
}