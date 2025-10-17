package ai.shreds.product_management_availability_shred_g5ds.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain value object representing a unique product identifier.
 * Immutable and ensures UUID validation.
 */
public class DomainValueProductId {
    private final UUID value;

    /**
     * Constructs a ProductId with the given UUID value.
     * @param value the UUID value
     * @throws IllegalArgumentException if value is null
     */
    public DomainValueProductId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ProductId value cannot be null");
        }
        this.value = value;
    }

    /**
     * Generates a new ProductId with a random UUID.
     * @return a new ProductId instance
     */
    public static DomainValueProductId generate() {
        return new DomainValueProductId(UUID.randomUUID());
    }

    /**
     * Creates a ProductId from a string representation.
     * @param value the string UUID representation
     * @return a new ProductId instance
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static DomainValueProductId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ProductId string value cannot be null or empty");
        }
        try {
            return new DomainValueProductId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for ProductId: " + value, e);
        }
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainValueProductId that = (DomainValueProductId) other;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}