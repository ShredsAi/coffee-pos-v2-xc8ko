package ai.shreds.product_management_availability_shred_g5ds.application.services;

import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOAvailabilityUpdate;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOInventoryData;
import ai.shreds.product_management_availability_shred_g5ds.application.ports.ApplicationInputPortAvailabilityManagement;
import ai.shreds.product_management_availability_shred_g5ds.application.ports.ApplicationOutputPortEventPublisher;
import ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionProductNotFound;
import ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionBusinessRule;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainInputPortAvailabilityManagement;
import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityAvailability;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueAvailabilityId;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionProductNotFound;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionBusinessRuleViolation;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventAvailabilityChanged;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedAvailabilityDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationServiceAvailability implements ApplicationInputPortAvailabilityManagement {
    
    private final DomainInputPortAvailabilityManagement availabilityInputPort;
    private final ApplicationOutputPortEventPublisher eventPublisher;
    
    public ApplicationServiceAvailability(DomainInputPortAvailabilityManagement availabilityInputPort,
                                        ApplicationOutputPortEventPublisher eventPublisher) {
        this.availabilityInputPort = availabilityInputPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<SharedAvailabilityDTO> getAvailabilityByLocation(UUID locationId) {
        try {
            DomainValueLocationId domainLocationId = DomainValueLocationId.fromString(locationId.toString());
            List<DomainEntityAvailability> availabilities = availabilityInputPort.getAvailabilityByLocation(domainLocationId);
            
            return availabilities.stream()
                .map(this::mapDomainEntityToSharedDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            throw new ApplicationExceptionBusinessRule(
                "LocationAvailabilityRetrieval", 
                "Failed to retrieve availability for location: " + locationId + ". " + e.getMessage()
            );
        }
    }

    @Override
    public SharedAvailabilityDTO updateAvailability(UUID locationId, UUID productId, ApplicationDTOAvailabilityUpdate request) {
        try {
            DomainValueProductId domainProductId = DomainValueProductId.fromString(productId.toString());
            DomainValueLocationId domainLocationId = DomainValueLocationId.fromString(locationId.toString());
            
            // Get existing availability record
            DomainEntityAvailability existingAvailability = availabilityInputPort.checkProductAvailability(domainProductId, domainLocationId);
            
            // Store original values for event comparison
            boolean originalAvailability = existingAvailability.getIsAvailable();
            Integer originalQuantity = existingAvailability.getEstimatedQuantity();
            
            // Update availability through domain layer
            DomainEntityAvailability updatedAvailability;
            
            if (request.getIsAvailable()) {
                // Product is available - update with quantity
                updatedAvailability = availabilityInputPort.updateAvailability(
                    existingAvailability.getId(),
                    request.getIsAvailable(),
                    request.getEstimatedQuantity() != null ? request.getEstimatedQuantity() : 0
                );
            } else {
                // Product is unavailable - set unavailable with reason and restock time
                updatedAvailability = existingAvailability;
                
                // Use domain entity method to set unavailable status
                DomainEventAvailabilityChanged event = updatedAvailability.setUnavailable(
                    request.getUnavailableReason(),
                    request.getEstimatedRestockTime()
                );
                
                // Publish the event if availability changed
                if (event != null) {
                    eventPublisher.publishAvailabilityChangedEvent(event);
                }
            }
            
            // Check if availability status or quantity changed significantly
            boolean availabilityChanged = originalAvailability != updatedAvailability.getIsAvailable() ||
                !originalQuantity.equals(updatedAvailability.getEstimatedQuantity());
            
            if (availabilityChanged) {
                DomainEventAvailabilityChanged changeEvent = new DomainEventAvailabilityChanged(
                    domainProductId,
                    domainLocationId,
                    updatedAvailability.getIsAvailable(),
                    updatedAvailability.getEstimatedQuantity(),
                    updatedAvailability.getUnavailableReason(),
                    updatedAvailability.getEstimatedRestockTime()
                );
                
                eventPublisher.publishAvailabilityChangedEvent(changeEvent);
            }
            
            return mapDomainEntityToSharedDTO(updatedAvailability);
            
        } catch (DomainExceptionProductNotFound e) {
            throw new ApplicationExceptionProductNotFound(e.getProductId().getValue());
        } catch (DomainExceptionBusinessRuleViolation e) {
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        } catch (Exception e) {
            throw new ApplicationExceptionBusinessRule(
                "AvailabilityUpdate", 
                "Failed to update availability for product " + productId + " at location " + locationId + ": " + e.getMessage()
            );
        }
    }

    @Override
    public void processInventoryUpdate(UUID locationId, List<ApplicationDTOInventoryData> inventoryData) {
        try {
            DomainValueLocationId domainLocationId = DomainValueLocationId.fromString(locationId.toString());
            
            // Convert inventory data to domain format
            Map<DomainValueProductId, Integer> inventoryMap = new HashMap<>();
            for (ApplicationDTOInventoryData data : inventoryData) {
                DomainValueProductId productId = DomainValueProductId.fromString(data.getProductId().toString());
                inventoryMap.put(productId, data.getCurrentStock());
            }
            
            // Process inventory update through domain layer
            availabilityInputPort.processInventoryUpdate(domainLocationId, inventoryMap);
            
        } catch (DomainExceptionBusinessRuleViolation e) {
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        } catch (Exception e) {
            throw new ApplicationExceptionBusinessRule(
                "InventorySync", 
                "Failed to process inventory update for location " + locationId + ": " + e.getMessage()
            );
        }
    }

    @Override
    public SharedAvailabilityDTO checkProductAvailability(UUID productId, UUID locationId) {
        try {
            DomainValueProductId domainProductId = DomainValueProductId.fromString(productId.toString());
            DomainValueLocationId domainLocationId = DomainValueLocationId.fromString(locationId.toString());
            
            DomainEntityAvailability availability = availabilityInputPort.checkProductAvailability(domainProductId, domainLocationId);
            return mapDomainEntityToSharedDTO(availability);
            
        } catch (DomainExceptionProductNotFound e) {
            throw new ApplicationExceptionProductNotFound(e.getProductId().getValue());
        } catch (Exception e) {
            throw new ApplicationExceptionBusinessRule(
                "AvailabilityCheck", 
                "Failed to check availability for product " + productId + " at location " + locationId + ": " + e.getMessage()
            );
        }
    }
    
    public SharedAvailabilityDTO mapDomainEntityToSharedDTO(DomainEntityAvailability availability) {
        return new SharedAvailabilityDTO(
            availability.getId().getValue(),
            availability.getProductId().getValue(),
            availability.getLocationId().getValue(),
            availability.getIsAvailable(),
            availability.getEstimatedQuantity(),
            availability.getUnavailableReason(),
            availability.getLastUpdated(),
            availability.getEstimatedRestockTime()
        );
    }
}