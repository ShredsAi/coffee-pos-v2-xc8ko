package ai.shreds.product_management_availability_shred_g5ds.application;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ApplicationDTOInventoryResponse {
    
    @NotNull(message = "Location ID is required")
    private UUID locationId;
    
    @NotNull(message = "Inventory data is required")
    private List<ApplicationDTOInventoryData> inventoryData;
    
    private LocalDateTime timestamp;

    // Default constructor
    public ApplicationDTOInventoryResponse() {}

    // Constructor with required fields
    public ApplicationDTOInventoryResponse(UUID locationId, List<ApplicationDTOInventoryData> inventoryData) {
        this.locationId = locationId;
        this.inventoryData = inventoryData;
        this.timestamp = LocalDateTime.now();
    }

    // Full constructor
    public ApplicationDTOInventoryResponse(UUID locationId, List<ApplicationDTOInventoryData> inventoryData, LocalDateTime timestamp) {
        this.locationId = locationId;
        this.inventoryData = inventoryData;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public List<ApplicationDTOInventoryData> getInventoryData() {
        return inventoryData;
    }

    public void setInventoryData(List<ApplicationDTOInventoryData> inventoryData) {
        this.inventoryData = inventoryData;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}