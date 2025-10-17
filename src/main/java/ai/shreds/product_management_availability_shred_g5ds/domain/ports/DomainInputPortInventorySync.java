package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.services.DomainServiceInventorySync;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;

import java.util.List;
import java.util.Map;

/**
 * Domain input port for inventory synchronization operations.
 * Acts as a facade for inventory sync-related domain services and provides
 * a clean interface for the application layer.
 */
public class DomainInputPortInventorySync {
    private final DomainServiceInventorySync inventorySyncService;

    /**
     * Constructs the InventorySync domain input port.
     * @param inventorySyncService the inventory sync domain service
     */
    public DomainInputPortInventorySync(DomainServiceInventorySync inventorySyncService) {
        if (inventorySyncService == null) {
            throw new IllegalArgumentException("InventorySyncService cannot be null");
        }
        
        this.inventorySyncService = inventorySyncService;
    }

    /**
     * Polls inventory status from external service for all locations.
     * This method is typically called by a scheduled task.
     */
    public void pollInventoryStatus() {
        inventorySyncService.pollInventoryStatus();
    }

    /**
     * Synchronizes inventory for a specific location.
     * @param locationId the location identifier
     * @throws IllegalArgumentException if locationId is null
     * @throws RuntimeException if synchronization fails
     */
    public void syncInventoryForLocation(DomainValueLocationId locationId) {
        inventorySyncService.syncInventoryForLocation(locationId);
    }

    /**
     * Processes inventory response data and updates availability.
     * @param locationId the location identifier
     * @param inventoryData the inventory data map
     * @throws IllegalArgumentException if parameters are null
     * @throws RuntimeException if processing fails
     */
    public void processInventoryResponse(DomainValueLocationId locationId, 
                                        Map<DomainValueProductId, Integer> inventoryData) {
        inventorySyncService.processInventoryResponse(locationId, inventoryData);
    }

    /**
     * Handles failures when communicating with the inventory service.
     * @param locationId the location identifier (may be null for global failures)
     * @param error the error that occurred
     */
    public void handleInventoryServiceFailure(DomainValueLocationId locationId, Throwable error) {
        inventorySyncService.handleInventoryServiceFailure(locationId, error);
    }

    /**
     * Synchronizes inventory for specific products at a location.
     * @param locationId the location identifier
     * @param productIds the list of product IDs to sync
     * @throws IllegalArgumentException if parameters are null or invalid
     * @throws RuntimeException if synchronization fails
     */
    public void syncSpecificProducts(DomainValueLocationId locationId, List<DomainValueProductId> productIds) {
        inventorySyncService.syncSpecificProducts(locationId, productIds);
    }

    /**
     * Checks if the inventory service is currently available.
     * @return true if service is available, false otherwise
     */
    public boolean isInventoryServiceHealthy() {
        return inventorySyncService.isInventoryServiceHealthy();
    }

    /**
     * Performs a health check and logs the status.
     * This method is typically called by a scheduled health check task.
     */
    public void performHealthCheck() {
        inventorySyncService.performHealthCheck();
    }

    /**
     * Handles cleanup tasks related to inventory synchronization.
     * This method can be called periodically to clean up old data or reset counters.
     */
    public void performCleanupTasks() {
        inventorySyncService.performCleanupTasks();
    }

    /**
     * Initiates a full inventory synchronization for all locations.
     * This is a comprehensive operation that should be used sparingly.
     */
    public void performFullInventorySync() {
        try {
            pollInventoryStatus();
        } catch (Exception e) {
            handleInventoryServiceFailure(null, e);
            throw new RuntimeException("Full inventory sync failed", e);
        }
    }

    /**
     * Initiates an emergency inventory sync for a specific location.
     * This method provides faster response for critical inventory situations.
     * @param locationId the location identifier
     * @param productIds the priority product IDs to sync first (optional)
     */
    public void performEmergencySync(DomainValueLocationId locationId, List<DomainValueProductId> productIds) {
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null for emergency sync");
        }
        
        try {
            if (productIds != null && !productIds.isEmpty()) {
                // Sync specific products first
                syncSpecificProducts(locationId, productIds);
            }
            // Then sync the entire location
            syncInventoryForLocation(locationId);
        } catch (Exception e) {
            handleInventoryServiceFailure(locationId, e);
            throw new RuntimeException("Emergency inventory sync failed for location: " + locationId, e);
        }
    }

    /**
     * Validates that inventory synchronization can proceed.
     * @return true if sync can proceed, false otherwise
     */
    public boolean canPerformSync() {
        return isInventoryServiceHealthy();
    }

    /**
     * Gets the current status of inventory synchronization operations.
     * @return a status message indicating the current state
     */
    public String getSyncStatus() {
        boolean healthy = isInventoryServiceHealthy();
        return healthy ? "Inventory sync service is healthy and operational" 
                      : "Inventory sync service is experiencing issues";
    }

    /**
     * Schedules a delayed inventory sync for a location.
     * This method is useful for implementing retry logic with backoff.
     * @param locationId the location identifier
     * @param delayMinutes the delay in minutes before attempting sync
     */
    public void scheduleDelayedSync(DomainValueLocationId locationId, int delayMinutes) {
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        if (delayMinutes < 0) {
            throw new IllegalArgumentException("Delay minutes cannot be negative");
        }
        
        // In a real implementation, this would use a scheduling service
        // For now, we'll just log the intent
        System.out.println(String.format("Scheduled inventory sync for location %s in %d minutes", 
                locationId, delayMinutes));
    }

    /**
     * Cancels any pending inventory synchronization operations for a location.
     * @param locationId the location identifier
     */
    public void cancelPendingSync(DomainValueLocationId locationId) {
        if (locationId == null) {
            throw new IllegalArgumentException("LocationId cannot be null");
        }
        
        // In a real implementation, this would cancel scheduled tasks
        // For now, we'll just log the intent
        System.out.println("Cancelled pending inventory sync for location: " + locationId);
    }
}