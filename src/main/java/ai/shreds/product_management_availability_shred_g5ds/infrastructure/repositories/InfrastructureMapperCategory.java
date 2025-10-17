package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityCategory;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InfrastructureMapperCategory {

    public DomainEntityCategory toDomain(InfrastructureEntityCategory entity) {
        if (entity == null) {
            return null;
        }
        
        // Create domain entity using proper constructor
        DomainEntityCategory category = new DomainEntityCategory(
            entity.getName(),
            entity.getDisplayOrder() != null ? entity.getDisplayOrder() : 0
        );
        
        // Override the generated categoryId with the persisted one
        // This is necessary since the entity constructor generates a new ID but we need the persisted one
        try {
            java.lang.reflect.Field idField = DomainEntityCategory.class.getDeclaredField("categoryId");
            idField.setAccessible(true);
            idField.set(category, new DomainValueCategoryId(entity.getCategoryId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set category ID", e);
        }
        
        // Set description using the proper method
        if (entity.getDescription() != null) {
            category.updateDescription(entity.getDescription());
        }
        
        // Set active status - we need to use reflection here as there's no public setter
        // but we need to preserve the database state
        try {
            java.lang.reflect.Field activeField = DomainEntityCategory.class.getDeclaredField("isActive");
            activeField.setAccessible(true);
            activeField.set(category, entity.getIsActive() != null ? entity.getIsActive() : true);
        } catch (Exception e) {
            // If we can't set the active status, default behavior is active=true from constructor
        }
        
        // Note: Parent category relationship should be set by caller who has access to other categories
        // We don't try to create circular dependencies here
        
        return category;
    }

    public InfrastructureEntityCategory toEntity(DomainEntityCategory domain) {
        if (domain == null) {
            return null;
        }
        
        InfrastructureEntityCategory entity = new InfrastructureEntityCategory();
        
        // Set all fields using domain getters
        if (domain.getId() != null) {
            entity.setCategoryId(domain.getId().getValue());
        }
        
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setDisplayOrder(domain.getDisplayOrder());
        entity.setIsActive(domain.getIsActive() != null ? domain.getIsActive() : true);
        
        // Handle parent category if exists
        if (domain.getParentCategory() != null && domain.getParentCategory().getId() != null) {
            entity.setParentCategoryId(domain.getParentCategory().getId().getValue());
        }
        
        return entity;
    }

    public List<DomainEntityCategory> toDomainList(List<InfrastructureEntityCategory> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<InfrastructureEntityCategory> toEntityList(List<DomainEntityCategory> domains) {
        if (domains == null) {
            return new ArrayList<>();
        }
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Creates a domain category with the specified ID.
     * Used for reconstructing categories from persistence with their original IDs.
     * 
     * @param entity the infrastructure entity from database
     * @param parentCategory optional parent category to set
     * @return domain entity with correct ID and parent relationship
     */
    public DomainEntityCategory toDomainWithParent(InfrastructureEntityCategory entity, DomainEntityCategory parentCategory) {
        DomainEntityCategory category = toDomain(entity);
        if (parentCategory != null) {
            category.setParentCategory(parentCategory);
        }
        return category;
    }
}