package ai.shreds.product_management_availability_shred_g5ds.application.services;

import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOInventoryData;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOInventoryResponse;
import ai.shreds.product_management_availability_shred_g5ds.application.ports.ApplicationInputPortInventorySync;
import ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionInventorySync;
import ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionBusinessRule;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainInputPortInventorySync;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionBusinessRuleViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ApplicationServiceInventorySync implements ApplicationInputPortInventorySync {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceInventorySync.class);
    
    private final DomainInputPortInventorySync inventorySyncInputPort;
    private final ApplicationServiceAvailability availabilityService;
    
    public ApplicationServiceInventorySync(DomainInputPortInventorySync inventorySyncInputPort,
                                         ApplicationServiceAvailability availabilityService) {
        this.inventorySyncInputPort = inventorySyncInputPort;
        this.availabilityService = availabilityService;
    }

    @Override
    public void pollInventoryStatus() {
        try {
            logger.info("Starting inventory status polling for all locations");
            inventorySyncInputPort.pollInventoryStatus();
            logger.info("Completed inventory status polling for all locations");
            
        } catch (DomainExceptionBusinessRuleViolation e) {
            logger.error("Business rule violation during inventory polling: {} - {}", e.getRuleName(), e.getViolatedConstraint());
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        } catch (Exception e) {
            logger.error("Failed to poll inventory status for all locations", e);
            throw new ApplicationExceptionInventorySync(
                "Failed to poll inventory status: " + e.getMessage(),
                null, // No specific location for global polling
                "POLLING_FAILURE"
            );
        }
    }

    @Override
    public void syncInventoryForLocation(UUID locationId) {
        try {
            if (locationId == null) {
                throw new ApplicationExceptionBusinessRule(
                    "InvalidInput", 
                    "Location ID cannot be null for inventory sync"
                );
            }
            
            DomainValueLocationId domainLocationId = DomainValueLocationId.fromString(locationId.toString());
            
            logger.info("Starting inventory sync for location: {}", locationId);
            inventorySyncInputPort.syncInventoryForLocation(domainLocationId);
            logger.info("Completed inventory sync for location: {}", locationId);
            
        } catch (DomainExceptionBusinessRuleViolation e) {
            logger.error("Business rule violation during inventory sync for location {}: {} - {}", 
                locationId, e.getRuleName(), e.getViolatedConstraint());
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        } catch (ApplicationExceptionBusinessRule e) {
            // Re-throw application exceptions
            throw e;
        } catch (Exception e) {
            logger.error("Failed to sync inventory for location: {}", locationId, e);
            throw new ApplicationExceptionInventorySync(
                "Failed to sync inventory for location " + locationId + ": " + e.getMessage(),
                locationId,
                "SYNC_FAILURE"
            );
        }
    }

    @Override
    public void processInventoryResponse(UUID locationId, ApplicationDTOInventoryResponse inventoryResponse) {
        try {
            if (locationId == null) {
                throw new ApplicationExceptionBusinessRule(
                    "InvalidInput", 
                    "Location ID cannot be null for inventory response processing"
                );
            }
            
            if (inventoryResponse == null) {
                throw new ApplicationExceptionBusinessRule(
                    "InvalidInput", 
                    "Inventory response cannot be null"
                );
            }
            
            if (inventoryResponse.getInventoryData() == null || inventoryResponse.getInventoryData().isEmpty()) {
                logger.warn("Received empty inventory data for location: {}", locationId);
                return;
            }
            
            DomainValueLocationId domainLocationId = DomainValueLocationId.fromString(locationId.toString());
            
            // Convert inventory response to domain format
            Map<DomainValueProductId, Integer> inventoryData = new HashMap<>();
            for (ApplicationDTOInventoryData data : inventoryResponse.getInventoryData()) {
                if (data.getProductId() != null && data.getCurrentStock() != null) {
                    DomainValueProductId productId = DomainValueProductId.fromString(data.getProductId().toString());
                    inventoryData.put(productId, data.getCurrentStock());
                } else {
                    logger.warn("Skipping invalid inventory data entry for location {}: productId={}, currentStock={}", 
                        locationId, data.getProductId(), data.getCurrentStock());
                }
            }
            
            if (inventoryData.isEmpty()) {
                logger.warn("No valid inventory data found after processing response for location: {}", locationId);
                return;
            }
            
            logger.info("Processing inventory response for location {} with {} products", 
                locationId, inventoryData.size());
            
            inventorySyncInputPort.processInventoryResponse(domainLocationId, inventoryData);
            
            logger.info("Successfully processed inventory response for location: {}", locationId);
            
        } catch (DomainExceptionBusinessRuleViolation e) {
            logger.error("Business rule violation during inventory response processing for location {}: {} - {}", 
                locationId, e.getRuleName(), e.getViolatedConstraint());
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        } catch (ApplicationExceptionBusinessRule e) {
            // Re-throw application exceptions
            throw e;
        } catch (Exception e) {
            logger.error("Failed to process inventory response for location: {}", locationId, e);
            throw new ApplicationExceptionInventorySync(
                "Failed to process inventory response for location " + locationId + ": " + e.getMessage(),
                locationId,
                "RESPONSE_PROCESSING_FAILURE"
            );
        }
    }

    @Override
    public void handleInventoryServiceFailure(UUID locationId, Throwable error) {
        try {
            if (error == null) {
                logger.warn("Inventory service failure handler called with null error for location: {}", locationId);
                return;
            }
            
            DomainValueLocationId domainLocationId = locationId != null ? 
                DomainValueLocationId.fromString(locationId.toString()) : null;
            
            logger.error("Handling inventory service failure for location: {} - Error: {}", 
                locationId, error.getMessage(), error);
            
            inventorySyncInputPort.handleInventoryServiceFailure(domainLocationId, error);
            
            logger.info("Completed handling inventory service failure for location: {}", locationId);
            
        } catch (DomainExceptionBusinessRuleViolation e) {
            logger.error("Business rule violation during inventory service failure handling for location {}: {} - {}", 
                locationId, e.getRuleName(), e.getViolatedConstraint());
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        } catch (Exception e) {
            logger.error("Failed to handle inventory service failure for location: {}", locationId, e);
            throw new ApplicationExceptionInventorySync(
                "Failed to handle inventory service failure for location " + locationId + ": " + e.getMessage(),
                locationId,
                "FAILURE_HANDLING_ERROR"
            );
        }
    }
}