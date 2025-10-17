package ai.shreds.product_management_availability_shred_g5ds.domain;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain event representing the update of an existing product.
 * Immutable event that captures product changes with before/after values.
 */
public class DomainEventProductUpdated {
    private final DomainValueProductId productId;
    private final String productName;
    private final Map<String, Object> changes;
    private final LocalDateTime timestamp;

    /**
     * Constructs a ProductUpdated domain event.
     * @param productId the unique identifier of the updated product
     * @param productName the name of the updated product
     * @param changes map containing field changes with old/new values
     */
    public DomainEventProductUpdated(DomainValueProductId productId, String productName, 
                                   Map<String, Object> changes) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (changes == null) {
            throw new IllegalArgumentException("Changes cannot be null");
        }
        
        this.productId = productId;
        this.productName = productName;
        this.changes = new HashMap<>(changes);
        this.timestamp = LocalDateTime.now();
    }

    public DomainValueProductId getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Map<String, Object> getChanges() {
        return new HashMap<>(changes);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Checks if a specific field was changed in this update.
     * @param fieldName the name of the field to check
     * @return true if the field was changed, false otherwise
     */
    public boolean hasFieldChanged(String fieldName) {
        return changes.containsKey(fieldName);
    }

    /**
     * Gets the new value for a specific field.
     * @param fieldName the name of the field
     * @return the new value or null if field wasn't changed
     */
    public Object getNewValue(String fieldName) {
        Map<String, Object> fieldChange = (Map<String, Object>) changes.get(fieldName);
        return fieldChange != null ? fieldChange.get("newValue") : null;
    }

    /**
     * Gets the old value for a specific field.
     * @param fieldName the name of the field
     * @return the old value or null if field wasn't changed
     */
    public Object getOldValue(String fieldName) {
        Map<String, Object> fieldChange = (Map<String, Object>) changes.get(fieldName);
        return fieldChange != null ? fieldChange.get("oldValue") : null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainEventProductUpdated that = (DomainEventProductUpdated) other;
        return Objects.equals(productId, that.productId) &&
               Objects.equals(productName, that.productName) &&
               Objects.equals(changes, that.changes) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, changes, timestamp);
    }

    @Override
    public String toString() {
        return String.format("DomainEventProductUpdated{productId=%s, productName='%s', changes=%s, timestamp=%s}",
            productId, productName, changes, timestamp);
    }
}