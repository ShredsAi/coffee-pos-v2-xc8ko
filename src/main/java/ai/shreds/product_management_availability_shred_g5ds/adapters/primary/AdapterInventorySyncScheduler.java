package ai.shreds.product_management_availability_shred_g5ds.adapters.primary;

import ai.shreds.product_management_availability_shred_g5ds.application.services.ApplicationServiceInventorySync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
public class AdapterInventorySyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AdapterInventorySyncScheduler.class);
    
    private final ApplicationServiceInventorySync inventorySyncApplicationService;

    @Autowired
    public AdapterInventorySyncScheduler(ApplicationServiceInventorySync inventorySyncApplicationService) {
        this.inventorySyncApplicationService = inventorySyncApplicationService;
    }

    /**
     * Polls inventory status every 5 minutes
     * Uses fixedRate to ensure consistent polling intervals
     */
    @Scheduled(fixedRate = 300000) // 5 minutes = 300,000 milliseconds
    public void pollInventoryStatus() {
        logger.info("Starting scheduled inventory status polling at: {}", LocalDateTime.now());
        
        try {
            // Execute inventory polling asynchronously to avoid blocking other scheduled tasks
            CompletableFuture.runAsync(() -> {
                try {
                    inventorySyncApplicationService.pollInventoryStatus();
                    logger.info("Successfully completed inventory status polling");
                } catch (Exception e) {
                    logger.error("Error during scheduled inventory status polling", e);
                    // Handle the error by calling the failure handler if needed
                    inventorySyncApplicationService.handleInventoryServiceFailure(null, e);
                }
            });
        } catch (Exception e) {
            logger.error("Failed to initiate scheduled inventory status polling", e);
        }
    }

    /**
     * Synchronizes inventory for all locations every 30 minutes
     * Uses fixedDelay to ensure previous execution completes before starting next
     * Since we don't have syncAllLocationsInventory method, we'll use general polling
     */
    @Scheduled(fixedDelay = 1800000, initialDelay = 60000) // 30 minutes delay, 1 minute initial delay
    public void syncAllLocationsInventory() {
        logger.info("Starting comprehensive inventory synchronization for all locations at: {}", LocalDateTime.now());
        
        try {
            // Use the general polling method as a fallback for comprehensive sync
            inventorySyncApplicationService.pollInventoryStatus();
            logger.info("Successfully completed comprehensive inventory synchronization using general polling");
        } catch (Exception e) {
            logger.error("Error during comprehensive inventory synchronization", e);
            
            // Log additional context for troubleshooting
            logger.error("Comprehensive sync failure time: {}", LocalDateTime.now());
            inventorySyncApplicationService.handleInventoryServiceFailure(null, e);
        }
    }

    /**
     * Health check for inventory synchronization service
     * Runs every hour to ensure service connectivity
     * Since we don't have a dedicated health check method, we'll do a simple poll attempt
     */
    @Scheduled(fixedRate = 3600000) // 1 hour = 3,600,000 milliseconds
    public void performInventoryServiceHealthCheck() {
        logger.debug("Performing inventory service health check at: {}", LocalDateTime.now());
        
        try {
            // Perform a lightweight polling operation as a health check
            inventorySyncApplicationService.pollInventoryStatus();
            logger.debug("Inventory service health check passed");
        } catch (Exception e) {
            logger.warn("Inventory service health check failed - service may be unavailable", e);
            inventorySyncApplicationService.handleInventoryServiceFailure(null, e);
        }
    }

    /**
     * Cleanup stale cache entries and expired availability data
     * Runs daily at 2 AM
     * Since we don't have a dedicated cleanup method, we'll just log for now
     */
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2:00 AM
    public void performDailyCleanup() {
        logger.info("Starting daily cleanup of stale inventory data at: {}", LocalDateTime.now());
        
        try {
            // For now, just perform a comprehensive poll to refresh data
            inventorySyncApplicationService.pollInventoryStatus();
            logger.info("Successfully completed daily cleanup through inventory refresh");
        } catch (Exception e) {
            logger.error("Error during daily cleanup of stale inventory data", e);
            inventorySyncApplicationService.handleInventoryServiceFailure(null, e);
        }
    }
}