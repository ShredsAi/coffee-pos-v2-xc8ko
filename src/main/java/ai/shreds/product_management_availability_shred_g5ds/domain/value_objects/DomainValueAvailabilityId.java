package ai.shreds.product_management_availability_shred_g5ds.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain value object representing a unique availability identifier.
 * Immutable and ensures UUID validation.
 */
public class DomainValueAvailabilityId {
    private final UUID value;

    /**
     * Constructs an AvailabilityId with the given UUID value.
     * @param value the UUID value
     * @throws IllegalArgumentException if value is null
     */
    public DomainValueAvailabilityId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("AvailabilityId value cannot be null");
        }
        this.value = value;
    }

    /**
     * Generates a new AvailabilityId with a random UUID.
     * @return a new AvailabilityId instance
     */
    public static DomainValueAvailabilityId generate() {
        return new DomainValueAvailabilityId(UUID.randomUUID());
    }

    /**
     * Creates an AvailabilityId from a string representation.
     * @param value the string UUID representation
     * @return a new AvailabilityId instance
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static DomainValueAvailabilityId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("AvailabilityId string value cannot be null or empty");
        }
        try {
            return new DomainValueAvailabilityId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for AvailabilityId: " + value, e);
        }
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainValueAvailabilityId that = (DomainValueAvailabilityId) other;
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