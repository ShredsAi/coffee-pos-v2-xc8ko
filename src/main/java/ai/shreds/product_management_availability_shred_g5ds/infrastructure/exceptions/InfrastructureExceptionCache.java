package ai.shreds.product_management_availability_shred_g5ds.infrastructure.exceptions;

public class InfrastructureExceptionCache extends RuntimeException {
    private final String cacheOperation;
    private final String cacheKey;

    public InfrastructureExceptionCache(String cacheOperation, String cacheKey) {
        super(String.format("Cache operation failed - Operation: %s, Key: %s", cacheOperation, cacheKey));
        this.cacheOperation = cacheOperation;
        this.cacheKey = cacheKey;
    }

    public InfrastructureExceptionCache(String message, String cacheOperation, String cacheKey) {
        super(message);
        this.cacheOperation = cacheOperation;
        this.cacheKey = cacheKey;
    }

    public InfrastructureExceptionCache(String message, Throwable cause, String cacheOperation, String cacheKey) {
        super(message, cause);
        this.cacheOperation = cacheOperation;
        this.cacheKey = cacheKey;
    }

    public String getCacheOperation() {
        return cacheOperation;
    }

    public String getCacheKey() {
        return cacheKey;
    }
}