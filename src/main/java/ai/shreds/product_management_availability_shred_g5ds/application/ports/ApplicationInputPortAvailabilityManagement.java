package ai.shreds.product_management_availability_shred_g5ds.application.ports;

import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOAvailabilityUpdate;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOInventoryData;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedAvailabilityDTO;

import java.util.List;
import java.util.UUID;

public interface ApplicationInputPortAvailabilityManagement {
    
    /**
     * Retrieves all product availability for a specific location
     * @param locationId The ID of the location to get availability for
     * @return List of availability DTOs for the location
     */
    List<SharedAvailabilityDTO> getAvailabilityByLocation(UUID locationId);
    
    /**
     * Updates the availability status of a product at a specific location
     * @param locationId The ID of the location
     * @param productId The ID of the product
     * @param request The availability update request
     * @return The updated availability DTO
     */
    SharedAvailabilityDTO updateAvailability(UUID locationId, UUID productId, ApplicationDTOAvailabilityUpdate request);
    
    /**
     * Processes inventory updates from external systems
     * @param locationId The ID of the location
     * @param inventoryData List of inventory data updates
     */
    void processInventoryUpdate(UUID locationId, List<ApplicationDTOInventoryData> inventoryData);
    
    /**
     * Checks the availability status of a specific product at a location
     * @param productId The ID of the product
     * @param locationId The ID of the location
     * @return The availability DTO for the product at the location
     */
    SharedAvailabilityDTO checkProductAvailability(UUID productId, UUID locationId);
}