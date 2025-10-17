package ai.shreds.product_management_availability_shred_g5ds.adapters.primary;

import ai.shreds.product_management_availability_shred_g5ds.application.services.ApplicationServiceAvailability;
import ai.shreds.product_management_availability_shred_g5ds.shared.utils.SharedUtilMapper;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOInventoryData;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOInventoryResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AdapterJMSMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(AdapterJMSMessageListener.class);
    
    private final ApplicationServiceAvailability availabilityApplicationService;
    private final SharedUtilMapper messageMapper;

    @Autowired
    public AdapterJMSMessageListener(ApplicationServiceAvailability availabilityApplicationService,
                                    SharedUtilMapper messageMapper) {
        this.availabilityApplicationService = availabilityApplicationService;
        this.messageMapper = messageMapper;
    }

    @JmsListener(destination = "seasonal.offerings.updates")
    public void handleSeasonalOfferingUpdates(String message) {
        try {
            logger.info("Received seasonal offering update message: {}", message);
            
            // Parse the message to extract seasonal offering updates
            // This could affect product availability based on seasonal rules
            ApplicationDTOInventoryResponse seasonalUpdate = messageMapper.fromJson(
                message, ApplicationDTOInventoryResponse.class);
            
            if (seasonalUpdate != null && seasonalUpdate.getLocationId() != null) {
                UUID locationId = seasonalUpdate.getLocationId();
                List<ApplicationDTOInventoryData> inventoryData = seasonalUpdate.getInventoryData();
                
                // Process the seasonal offering updates through availability service
                availabilityApplicationService.processInventoryUpdate(locationId, inventoryData);
                
                logger.info("Successfully processed seasonal offering updates for location: {}", locationId);
            }
        } catch (Exception e) {
            logger.error("Error processing seasonal offering update message: {}", message, e);
        }
    }

    @JmsListener(destination = "inventory.status.updates")
    public void handleInventoryStatusUpdates(String message) {
        try {
            logger.info("Received inventory status update message: {}", message);
            
            // Parse the inventory status update message
            ApplicationDTOInventoryResponse inventoryUpdate = messageMapper.fromJson(
                message, ApplicationDTOInventoryResponse.class);
            
            if (inventoryUpdate != null && inventoryUpdate.getLocationId() != null) {
                UUID locationId = inventoryUpdate.getLocationId();
                List<ApplicationDTOInventoryData> inventoryData = inventoryUpdate.getInventoryData();
                
                // Process the inventory updates through availability service
                availabilityApplicationService.processInventoryUpdate(locationId, inventoryData);
                
                logger.info("Successfully processed inventory status updates for location: {}", locationId);
            }
        } catch (Exception e) {
            logger.error("Error processing inventory status update message: {}", message, e);
        }
    }

    @JmsListener(destination = "product.restock.notifications")
    public void handleProductRestockNotifications(String message) {
        try {
            logger.info("Received product restock notification: {}", message);
            
            // Parse the restock notification message
            ApplicationDTOInventoryResponse restockNotification = messageMapper.fromJson(
                message, ApplicationDTOInventoryResponse.class);
            
            if (restockNotification != null && restockNotification.getLocationId() != null) {
                UUID locationId = restockNotification.getLocationId();
                List<ApplicationDTOInventoryData> inventoryData = restockNotification.getInventoryData();
                
                // Process the restock notifications
                availabilityApplicationService.processInventoryUpdate(locationId, inventoryData);
                
                logger.info("Successfully processed restock notifications for location: {}", locationId);
            }
        } catch (Exception e) {
            logger.error("Error processing product restock notification: {}", message, e);
        }
    }
}