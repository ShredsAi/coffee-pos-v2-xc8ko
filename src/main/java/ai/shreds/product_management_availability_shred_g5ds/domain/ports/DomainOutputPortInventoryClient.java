package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;

import java.util.List;
import java.util.Map;

/**
 * Domain output port for external inventory management service integration.
 * Defines the contract for retrieving inventory data from external systems.
 */
public interface DomainOutputPortInventoryClient {
    
    /**
     * Gets the current inventory status for specific products at a location.
     * @param locationId the location identifier
     * @param productIds list of product identifiers to check
     * @return map of product ID to current inventory quantity
     */
    Map<DomainValueProductId, Integer> getInventoryStatus(DomainValueLocationId locationId, List<DomainValueProductId> productIds);
    
    /**
     * Gets the inventory status for all products at all locations.
     * @return map of location ID to map of product ID to inventory quantity
     */
    Map<DomainValueLocationId, Map<DomainValueProductId, Integer>> getAllLocationsInventoryStatus();
    
    /**
     * Checks if the external inventory service is available.
     * @return true if the service is responsive and available
     */
    boolean isServiceAvailable();
}