package ai.shreds.product_management_availability_shred_g5ds.application;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class ApplicationDTOInventoryData {
    
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    @Min(value = 0, message = "Current stock must be non-negative")
    private Integer currentStock;
    
    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;
    
    private LocalDateTime lastUpdated;

    // Default constructor
    public ApplicationDTOInventoryData() {}

    // Constructor with required fields
    public ApplicationDTOInventoryData(UUID productId, Integer currentStock, Boolean isAvailable) {
        this.productId = productId;
        this.currentStock = currentStock;
        this.isAvailable = isAvailable;
    }

    // Full constructor
    public ApplicationDTOInventoryData(UUID productId, Integer currentStock, Boolean isAvailable, LocalDateTime lastUpdated) {
        this.productId = productId;
        this.currentStock = currentStock;
        this.isAvailable = isAvailable;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}