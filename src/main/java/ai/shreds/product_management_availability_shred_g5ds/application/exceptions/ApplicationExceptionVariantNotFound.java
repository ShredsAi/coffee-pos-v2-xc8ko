package ai.shreds.product_management_availability_shred_g5ds.application.exceptions;

import java.util.UUID;

public class ApplicationExceptionVariantNotFound extends RuntimeException {
    
    private final UUID variantId;
    private final UUID productId;
    
    public ApplicationExceptionVariantNotFound(UUID variantId, UUID productId) {
        super(String.format("Variant with ID %s not found for product %s", variantId, productId));
        this.variantId = variantId;
        this.productId = productId;
    }
    
    public ApplicationExceptionVariantNotFound(String message, UUID variantId, UUID productId) {
        super(message);
        this.variantId = variantId;
        this.productId = productId;
    }
    
    public ApplicationExceptionVariantNotFound(String message, UUID variantId, UUID productId, Throwable cause) {
        super(message, cause);
        this.variantId = variantId;
        this.productId = productId;
    }
    
    public UUID getVariantId() {
        return variantId;
    }
    
    public UUID getProductId() {
        return productId;
    }
}