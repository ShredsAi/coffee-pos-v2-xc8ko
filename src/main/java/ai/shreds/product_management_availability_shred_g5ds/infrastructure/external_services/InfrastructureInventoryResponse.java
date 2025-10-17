package ai.shreds.product_management_availability_shred_g5ds.infrastructure.external_services;

import java.util.List;

public class InfrastructureInventoryResponse {
    private String locationId;
    private List<InfrastructureInventoryItemData> inventoryData;
    private String timestamp;

    public InfrastructureInventoryResponse() {}

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public List<InfrastructureInventoryItemData> getInventoryData() {
        return inventoryData;
    }

    public void setInventoryData(List<InfrastructureInventoryItemData> inventoryData) {
        this.inventoryData = inventoryData;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}