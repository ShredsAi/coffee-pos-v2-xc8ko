package ai.shreds.product_management_availability_shred_g5ds.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain value object representing a unique location identifier.
 * Immutable and ensures UUID validation.
 */
public class DomainValueLocationId {
    private final UUID value;

    /**
     * Constructs a LocationId with the given UUID value.
     * @param value the UUID value
     * @throws IllegalArgumentException if value is null
     */
    public DomainValueLocationId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("LocationId value cannot be null");
        }
        this.value = value;
    }

    /**
     * Generates a new LocationId with a random UUID.
     * @return a new LocationId instance
     */
    public static DomainValueLocationId generate() {
        return new DomainValueLocationId(UUID.randomUUID());
    }

    /**
     * Creates a LocationId from a string representation.
     * @param value the string UUID representation
     * @return a new LocationId instance
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static DomainValueLocationId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("LocationId string value cannot be null or empty");
        }
        try {
            return new DomainValueLocationId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for LocationId: " + value, e);
        }
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainValueLocationId that = (DomainValueLocationId) other;
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