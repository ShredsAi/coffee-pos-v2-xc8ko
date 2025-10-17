package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityAvailability;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueAvailabilityId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;

import java.util.List;

/**
 * Domain output port for availability repository operations.
 * Defines the contract for availability persistence and retrieval.
 */
public interface DomainOutputPortAvailabilityRepository {
    
    /**
     * Saves an availability entity.
     * @param availability the availability to save
     * @return the saved availability
     */
    DomainEntityAvailability save(DomainEntityAvailability availability);
    
    /**
     * Finds an availability record by its ID.
     * @param availabilityId the availability identifier
     * @return the availability entity or null if not found
     */
    DomainEntityAvailability findById(DomainValueAvailabilityId availabilityId);
    
    /**
     * Finds all availability records for a specific location.
     * @param locationId the location identifier
     * @return list of availability records for the location
     */
    List<DomainEntityAvailability> findByLocationId(DomainValueLocationId locationId);
    
    /**
     * Finds the availability record for a specific product at a specific location.
     * @param productId the product identifier
     * @param locationId the location identifier
     * @return the availability entity or null if not found
     */
    DomainEntityAvailability findByProductAndLocation(DomainValueProductId productId, DomainValueLocationId locationId);
    
    /**
     * Deletes an availability record by its ID.
     * @param availabilityId the availability identifier
     */
    void delete(DomainValueAvailabilityId availabilityId);
}