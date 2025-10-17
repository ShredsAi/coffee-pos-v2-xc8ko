package ai.shreds.product_management_availability_shred_g5ds.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain value object representing a unique variant identifier.
 * Immutable and ensures UUID validation.
 */
public class DomainValueVariantId {
    private final UUID value;

    /**
     * Constructs a VariantId with the given UUID value.
     * @param value the UUID value
     * @throws IllegalArgumentException if value is null
     */
    public DomainValueVariantId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("VariantId value cannot be null");
        }
        this.value = value;
    }

    /**
     * Generates a new VariantId with a random UUID.
     * @return a new VariantId instance
     */
    public static DomainValueVariantId generate() {
        return new DomainValueVariantId(UUID.randomUUID());
    }

    /**
     * Creates a VariantId from a string representation.
     * @param value the string UUID representation
     * @return a new VariantId instance
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static DomainValueVariantId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("VariantId string value cannot be null or empty");
        }
        try {
            return new DomainValueVariantId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for VariantId: " + value, e);
        }
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainValueVariantId that = (DomainValueVariantId) other;
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