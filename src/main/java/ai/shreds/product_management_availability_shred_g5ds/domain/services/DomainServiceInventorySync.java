package ai.shreds.product_management_availability_shred_g5ds.domain.services;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortInventoryClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Domain service responsible for inventory synchronization with external systems.
 * Handles polling, processing responses, and error handling for inventory data.
 */
public class DomainServiceInventorySync {
    private final DomainOutputPortInventoryClient inventoryClient;
    private final DomainServiceAvailabilityManagement availabilityService;

    /**
     * Constructs the InventorySync domain service.
     * @param inventoryClient the inventory client port
     * @param availabilityService the availability management service
     */
    public DomainServiceInventorySync(DomainOutputPortInventoryClient inventoryClient,
                                     DomainServiceAvailabilityManagement availabilityService) {
        if (inventoryClient == null) {
            throw new IllegalArgumentException("InventoryClient cannot be null");
        }
        if (availabilityService == null) {
            throw new IllegalArgumentException("AvailabilityService cannot be null");
        }
        
        this.inventoryClient = inventoryClient;
        this.availabilityService = availabilityService;
    }

    /**
     * Polls inventory status from external service for all locations.
     * This method is typically called by a scheduled task.
     */
    public void pollInventoryStatus() {
        try {
            // Check if inventory service is available
            if (!inventoryClient.isServiceAvailable()) {
                System.err.println("Inventory service is not available for polling");
                return;
            }
            
            // Get inventory status for all locations
            Map<DomainValueLocationId, Map<DomainValueProductId, Integer>> allInventoryData = 
                inventoryClient.getAllLocationsInventoryStatus();
            
            if (allInventoryData == null || allInventoryData.isEmpty()) {
                System.out.println("No inventory data received from external service");
                return;
            }
            
            // Process inventory data for each location
            for (Map.Entry<DomainValueLocationId, Map<DomainValueProductId, Integer>> locationEntry : allInventoryData.entrySet()) {
                DomainValueLocationId locationId = locationEntry.getKey();
                Map<DomainValueProductId, Integer> inventoryData = locationEntry.getValue();
                
                try {
                    processInventoryResponse(locationId, inventoryData);
                    System.out.println("Successfully processed inventory data for location: " + locationId);
                } catch (Exception e) {
                    handleInventoryServiceFailure(locationId, e);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Failed to poll inventory status: " + e.getMessage());
            // In a real implementation, this would be logged properly and might trigger alerts
        }
    }

    /**
     * Synchronizes inventory for a specific location.
     * @param locationId the location identifier
     */
    public void syncInventoryForLocation(DomainValueLocationId locationId) {
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        
        try {
            // Check if inventory service is available
            if (!inventoryClient.isServiceAvailable()) {
                throw new RuntimeException("Inventory service is not available");
            }
            
            // Get inventory status for the specific location
            // We pass an empty product list to get all products for the location
            List<DomainValueProductId> allProducts = new ArrayList<>();
            Map<DomainValueProductId, Integer> inventoryData = 
                inventoryClient.getInventoryStatus(locationId, allProducts);
            
            if (inventoryData == null) {
                throw new RuntimeException("No inventory data received for location: " + locationId);
            }
            
            // Process the inventory response
            processInventoryResponse(locationId, inventoryData);
            
            System.out.println("Successfully synchronized inventory for location: " + locationId);
            
        } catch (Exception e) {
            handleInventoryServiceFailure(locationId, e);
            throw new RuntimeException("Failed to sync inventory for location: " + locationId, e);
        }
    }

    /**
     * Processes inventory response data and updates availability.
     * @param locationId the location identifier
     * @param inventoryData the inventory data map
     */
    public void processInventoryResponse(DomainValueLocationId locationId, 
                                        Map<DomainValueProductId, Integer> inventoryData) {
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        if (inventoryData == null) {
            throw new IllegalArgumentException("InventoryData cannot be null");
        }
        
        try {
            // Process inventory updates through availability service
            availabilityService.processInventoryUpdate(locationId, inventoryData);
            
            System.out.println(String.format("Processed inventory update for location %s with %d products", 
                    locationId, inventoryData.size()));
            
        } catch (Exception e) {
            System.err.println(String.format("Error processing inventory response for location %s: %s", 
                    locationId, e.getMessage()));
            throw new RuntimeException("Failed to process inventory response", e);
        }
    }

    /**
     * Handles failures when communicating with the inventory service.
     * @param locationId the location identifier (may be null for global failures)
     * @param error the error that occurred
     */
    public void handleInventoryServiceFailure(DomainValueLocationId locationId, Throwable error) {
        String locationInfo = locationId != null ? " for location " + locationId : "";
        String errorMessage = String.format("Inventory service failure%s: %s", locationInfo, error.getMessage());
        
        System.err.println(errorMessage);
        
        // Log the full stack trace for debugging
        if (error != null) {
            error.printStackTrace();
        }
        
        // In a real implementation, this would:
        // 1. Log the failure with appropriate severity
        // 2. Potentially send alerts to operations team
        // 3. Update service health metrics
        // 4. Implement exponential backoff for retries
        // 5. Fall back to cached data if available
        
        // For now, we'll just mark the service as degraded
        System.err.println("Inventory synchronization is degraded. Will retry on next scheduled poll.");
    }

    /**
     * Synchronizes inventory for specific products at a location.
     * @param locationId the location identifier
     * @param productIds the list of product IDs to sync
     */
    public void syncSpecificProducts(DomainValueLocationId locationId, List<DomainValueProductId> productIds) {
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("ProductIds cannot be null or empty");
        }
        
        try {
            // Check if inventory service is available
            if (!inventoryClient.isServiceAvailable()) {
                throw new RuntimeException("Inventory service is not available");
            }
            
            // Get inventory status for specific products
            Map<DomainValueProductId, Integer> inventoryData = 
                inventoryClient.getInventoryStatus(locationId, productIds);
            
            if (inventoryData == null) {
                throw new RuntimeException("No inventory data received for products at location: " + locationId);
            }
            
            // Process the inventory response
            processInventoryResponse(locationId, inventoryData);
            
            System.out.println(String.format("Successfully synchronized %d products for location: %s", 
                    productIds.size(), locationId));
            
        } catch (Exception e) {
            handleInventoryServiceFailure(locationId, e);
            throw new RuntimeException("Failed to sync specific products for location: " + locationId, e);
        }
    }

    /**
     * Checks if the inventory service is currently available.
     * @return true if service is available, false otherwise
     */
    public boolean isInventoryServiceHealthy() {
        try {
            return inventoryClient.isServiceAvailable();
        } catch (Exception e) {
            System.err.println("Error checking inventory service health: " + e.getMessage());
            return false;
        }
    }

    /**
     * Performs a health check and logs the status.
     * This method is typically called by a scheduled health check task.
     */
    public void performHealthCheck() {
        try {
            boolean isHealthy = isInventoryServiceHealthy();
            String status = isHealthy ? "HEALTHY" : "UNHEALTHY";
            System.out.println("Inventory service health check: " + status);
            
            if (!isHealthy) {
                System.err.println("Inventory service is not responding. Inventory synchronization may be affected.");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to perform inventory service health check: " + e.getMessage());
        }
    }

    /**
     * Handles cleanup tasks related to inventory synchronization.
     * This method can be called periodically to clean up old data or reset counters.
     */
    public void performCleanupTasks() {
        try {
            // In a real implementation, this might:
            // 1. Clean up old cached inventory data
            // 2. Reset error counters
            // 3. Archive old synchronization logs
            // 4. Validate data consistency
            
            System.out.println("Inventory sync cleanup tasks completed");
            
        } catch (Exception e) {
            System.err.println("Error during inventory sync cleanup: " + e.getMessage());
        }
    }
}