package ai.shreds.product_management_availability_shred_g5ds.infrastructure.external_services;

import java.util.List;

public class InfrastructureInventoryRequest {
    private String requestType;
    private String locationId;
    private List<String> productIds;
    private String timestamp;

    public InfrastructureInventoryRequest() {}

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}