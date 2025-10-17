package ai.shreds.product_management_availability_shred_g5ds.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain value object representing a unique category identifier.
 * Immutable and ensures UUID validation.
 */
public class DomainValueCategoryId {
    private final UUID value;

    /**
     * Constructs a CategoryId with the given UUID value.
     * @param value the UUID value
     * @throws IllegalArgumentException if value is null
     */
    public DomainValueCategoryId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("CategoryId value cannot be null");
        }
        this.value = value;
    }

    /**
     * Generates a new CategoryId with a random UUID.
     * @return a new CategoryId instance
     */
    public static DomainValueCategoryId generate() {
        return new DomainValueCategoryId(UUID.randomUUID());
    }

    /**
     * Creates a CategoryId from a string representation.
     * @param value the string UUID representation
     * @return a new CategoryId instance
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static DomainValueCategoryId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("CategoryId string value cannot be null or empty");
        }
        try {
            return new DomainValueCategoryId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for CategoryId: " + value, e);
        }
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainValueCategoryId that = (DomainValueCategoryId) other;
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