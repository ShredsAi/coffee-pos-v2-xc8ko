package ai.shreds.product_management_availability_shred_g5ds.application.ports;

import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOInventoryResponse;

import java.util.UUID;

public interface ApplicationInputPortInventorySync {
    
    /**
     * Polls the external inventory service for current status of all locations
     * This method is typically called by scheduled jobs
     */
    void pollInventoryStatus();
    
    /**
     * Synchronizes inventory data for a specific location
     * @param locationId The ID of the location to sync inventory for
     */
    void syncInventoryForLocation(UUID locationId);
    
    /**
     * Processes inventory response data from external service
     * @param locationId The ID of the location the data relates to
     * @param inventoryResponse The inventory response containing updated data
     */
    void processInventoryResponse(UUID locationId, ApplicationDTOInventoryResponse inventoryResponse);
    
    /**
     * Handles failures when communicating with external inventory service
     * @param locationId The ID of the location where the failure occurred
     * @param error The error that occurred during inventory service communication
     */
    void handleInventoryServiceFailure(UUID locationId, Throwable error);
}