package ai.shreds.product_management_availability_shred_g5ds.application.exceptions;

import java.util.UUID;

public class ApplicationExceptionInventorySync extends RuntimeException {
    
    private final UUID locationId;
    private final String errorType;
    
    public ApplicationExceptionInventorySync(UUID locationId, String errorType) {
        super(String.format("Inventory sync failed for location %s with error type: %s", locationId, errorType));
        this.locationId = locationId;
        this.errorType = errorType;
    }
    
    public ApplicationExceptionInventorySync(String message, UUID locationId, String errorType) {
        super(message);
        this.locationId = locationId;
        this.errorType = errorType;
    }
    
    public ApplicationExceptionInventorySync(String message, UUID locationId, String errorType, Throwable cause) {
        super(message, cause);
        this.locationId = locationId;
        this.errorType = errorType;
    }
    
    public UUID getLocationId() {
        return locationId;
    }
    
    public String getErrorType() {
        return errorType;
    }
}