package ai.shreds.product_management_availability_shred_g5ds.domain.exceptions;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;

/**
 * Domain exception thrown when a requested product is not found.
 * Contains the product ID that was not found for debugging and logging purposes.
 */
public class DomainExceptionProductNotFound extends RuntimeException {
    private final DomainValueProductId productId;

    /**
     * Constructs a new ProductNotFound exception with the specified product ID.
     * @param productId the ID of the product that was not found
     */
    public DomainExceptionProductNotFound(DomainValueProductId productId) {
        super(String.format("Product with ID '%s' was not found", productId));
        this.productId = productId;
    }

    /**
     * Constructs a new ProductNotFound exception with custom message and product ID.
     * @param message the custom error message
     * @param productId the ID of the product that was not found
     */
    public DomainExceptionProductNotFound(String message, DomainValueProductId productId) {
        super(message);
        this.productId = productId;
    }

    /**
     * Gets the product ID that was not found.
     * @return the product ID
     */
    public DomainValueProductId getProductId() {
        return productId;
    }

    @Override
    public String toString() {
        return String.format("DomainExceptionProductNotFound{productId=%s, message='%s'}", 
            productId, getMessage());
    }
}