package ai.shreds.product_management_availability_shred_g5ds.shared;

public class SharedExceptionNotFound extends RuntimeException {
    private final String resourceType;
    private final String resourceId;

    public SharedExceptionNotFound(String message) {
        super(message);
        this.resourceType = null;
        this.resourceId = null;
    }

    public SharedExceptionNotFound(String resourceType, String resourceId) {
        super(String.format("%s with id '%s' not found", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}