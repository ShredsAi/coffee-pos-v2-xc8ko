package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_availability")
public class InfrastructureEntityAvailability {
    
    @Id
    @Column(name = "availability_id")
    private UUID availabilityId;
    
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    
    @Column(name = "location_id", nullable = false)
    private UUID locationId;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    @Column(name = "estimated_quantity", nullable = false)
    private Integer estimatedQuantity = 0;
    
    @Column(name = "unavailable_reason", length = 30)
    private String unavailableReason;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(name = "estimated_restock_time")
    private LocalDateTime estimatedRestockTime;
    
    public InfrastructureEntityAvailability() {}
    
    @PrePersist
    protected void onCreate() {
        if (availabilityId == null) {
            availabilityId = UUID.randomUUID();
        }
        lastUpdated = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
    
    // Getters and setters
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
    
    public String getUnavailableReason() {
        return unavailableReason;
    }
    
    public void setUnavailableReason(String unavailableReason) {
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