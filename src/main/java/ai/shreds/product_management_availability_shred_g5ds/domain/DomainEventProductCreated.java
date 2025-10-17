package ai.shreds.product_management_availability_shred_g5ds.domain;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumProductType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain event representing the creation of a new product.
 * Immutable event that captures essential product creation details.
 */
public class DomainEventProductCreated {
    private final DomainValueProductId productId;
    private final String productName;
    private final DomainValueCategoryId categoryId;
    private final SharedEnumProductType productType;
    private final LocalDateTime timestamp;

    /**
     * Constructs a ProductCreated domain event.
     * @param productId the unique identifier of the created product
     * @param productName the name of the created product
     * @param categoryId the category identifier of the product
     * @param productType the type of the product
     */
    public DomainEventProductCreated(DomainValueProductId productId, String productName, 
                                   DomainValueCategoryId categoryId, SharedEnumProductType productType) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId cannot be null");
        }
        if (productType == null) {
            throw new IllegalArgumentException("ProductType cannot be null");
        }
        
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.productType = productType;
        this.timestamp = LocalDateTime.now();
    }

    public DomainValueProductId getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public DomainValueCategoryId getCategoryId() {
        return categoryId;
    }

    public SharedEnumProductType getProductType() {
        return productType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainEventProductCreated that = (DomainEventProductCreated) other;
        return Objects.equals(productId, that.productId) &&
               Objects.equals(productName, that.productName) &&
               Objects.equals(categoryId, that.categoryId) &&
               Objects.equals(productType, that.productType) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, categoryId, productType, timestamp);
    }

    @Override
    public String toString() {
        return String.format("DomainEventProductCreated{productId=%s, productName='%s', categoryId=%s, productType=%s, timestamp=%s}",
            productId, productName, categoryId, productType, timestamp);
    }
}