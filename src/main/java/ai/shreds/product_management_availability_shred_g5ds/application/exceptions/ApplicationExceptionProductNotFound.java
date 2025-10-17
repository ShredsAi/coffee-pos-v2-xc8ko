package ai.shreds.product_management_availability_shred_g5ds.application.exceptions;

import java.util.UUID;

public class ApplicationExceptionProductNotFound extends RuntimeException {
    
    private final UUID productId;
    
    public ApplicationExceptionProductNotFound(UUID productId) {
        super(String.format("Product with ID %s not found", productId));
        this.productId = productId;
    }
    
    public ApplicationExceptionProductNotFound(String message, UUID productId) {
        super(message);
        this.productId = productId;
    }
    
    public ApplicationExceptionProductNotFound(String message, UUID productId, Throwable cause) {
        super(message, cause);
        this.productId = productId;
    }
    
    public UUID getProductId() {
        return productId;
    }
}