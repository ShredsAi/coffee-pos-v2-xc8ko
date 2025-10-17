package ai.shreds.product_management_availability_shred_g5ds.infrastructure.exceptions;

public class InfrastructureExceptionDatabase extends RuntimeException {
    private final String operation;
    private final String entityType;

    public InfrastructureExceptionDatabase(String operation, String entityType) {
        super(String.format("Database operation failed - Operation: %s, EntityType: %s", operation, entityType));
        this.operation = operation;
        this.entityType = entityType;
    }

    public InfrastructureExceptionDatabase(String message, String operation, String entityType) {
        super(message);
        this.operation = operation;
        this.entityType = entityType;
    }

    public InfrastructureExceptionDatabase(String message, Throwable cause, String operation, String entityType) {
        super(message, cause);
        this.operation = operation;
        this.entityType = entityType;
    }

    public String getOperation() {
        return operation;
    }

    public String getEntityType() {
        return entityType;
    }
}