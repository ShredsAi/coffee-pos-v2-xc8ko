package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityAvailability;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.*;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumUnavailableReason;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InfrastructureMapperAvailability {

    public DomainEntityAvailability toDomain(InfrastructureEntityAvailability entity) {
        if (entity == null) {
            return null;
        }
        
        // Create value objects
        DomainValueProductId productId = new DomainValueProductId(entity.getProductId());
        DomainValueLocationId locationId = new DomainValueLocationId(entity.getLocationId());
        
        // Create domain entity using proper constructor
        DomainEntityAvailability availability = new DomainEntityAvailability(
            productId,
            locationId
        );
        
        // Override the generated availabilityId with the persisted one
        try {
            java.lang.reflect.Field idField = DomainEntityAvailability.class.getDeclaredField("availabilityId");
            idField.setAccessible(true);
            idField.set(availability, new DomainValueAvailabilityId(entity.getAvailabilityId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set availability ID", e);
        }
        
        // Update availability status using proper methods
        Boolean isAvailable = entity.getIsAvailable() != null ? entity.getIsAvailable() : true;
        Integer estimatedQuantity = entity.getEstimatedQuantity() != null ? entity.getEstimatedQuantity() : 0;
        
        // Use the updateAvailabilityStatus method
        availability.updateAvailabilityStatus(isAvailable, estimatedQuantity);
        
        // If there's an unavailable reason and the product is not available, use setUnavailable method
        if (!isAvailable && entity.getUnavailableReason() != null) {
            try {
                SharedEnumUnavailableReason reason = SharedEnumUnavailableReason.valueOf(entity.getUnavailableReason());
                availability.setUnavailable(reason, entity.getEstimatedRestockTime());
            } catch (IllegalArgumentException e) {
                // Invalid enum value, continue with basic availability update
            }
        }
        
        // Set timestamps using reflection since these fields need to preserve database values
        try {
            if (entity.getLastUpdated() != null) {
                java.lang.reflect.Field lastUpdatedField = DomainEntityAvailability.class.getDeclaredField("lastUpdated");
                lastUpdatedField.setAccessible(true);
                lastUpdatedField.set(availability, entity.getLastUpdated());
            }
        } catch (Exception e) {
            // If we can't set the timestamp, the entity will use the current time from the constructor
        }
        
        return availability;
    }

    public InfrastructureEntityAvailability toEntity(DomainEntityAvailability domain) {
        if (domain == null) {
            return null;
        }
        
        InfrastructureEntityAvailability entity = new InfrastructureEntityAvailability();
        
        // Set all fields using domain getters
        if (domain.getId() != null) {
            entity.setAvailabilityId(domain.getId().getValue());
        }
        
        if (domain.getProductId() != null) {
            entity.setProductId(domain.getProductId().getValue());
        }
        
        if (domain.getLocationId() != null) {
            entity.setLocationId(domain.getLocationId().getValue());
        }
        
        entity.setIsAvailable(domain.getIsAvailable() != null ? domain.getIsAvailable() : true);
        entity.setEstimatedQuantity(domain.getEstimatedQuantity() != null ? domain.getEstimatedQuantity() : 0);
        
        if (domain.getLastUpdated() != null) {
            entity.setLastUpdated(domain.getLastUpdated());
        }
        
        if (domain.getEstimatedRestockTime() != null) {
            entity.setEstimatedRestockTime(domain.getEstimatedRestockTime());
        }
        
        if (domain.getUnavailableReason() != null) {
            entity.setUnavailableReason(domain.getUnavailableReason().name());
        }
        
        return entity;
    }

    public List<DomainEntityAvailability> toDomainList(List<InfrastructureEntityAvailability> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<InfrastructureEntityAvailability> toEntityList(List<DomainEntityAvailability> domains) {
        if (domains == null) {
            return new ArrayList<>();
        }
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}