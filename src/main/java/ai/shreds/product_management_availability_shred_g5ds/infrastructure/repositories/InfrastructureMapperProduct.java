package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityProduct;
import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityCategory;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.*;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumProductType;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumCurrency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Component
public class InfrastructureMapperProduct {

    /**
     * Converts infrastructure entity to domain entity.
     * Note: This method requires a pre-loaded category entity to create the domain product.
     * 
     * @param entity the infrastructure entity
     * @param category the domain category entity (must be pre-loaded)
     * @return domain entity
     */
    public DomainEntityProduct toDomain(InfrastructureEntityProduct entity, DomainEntityCategory category) {
        if (entity == null) {
            return null;
        }
        
        if (category == null) {
            throw new IllegalArgumentException("Category is required to create domain product");
        }
        
        // Create value objects
        DomainValueMoney basePrice = new DomainValueMoney(
            entity.getBasePriceAmount(), 
            SharedEnumCurrency.valueOf(entity.getBasePriceCurrency())
        );
        
        // Create domain entity using proper constructor
        DomainEntityProduct product = new DomainEntityProduct(
            entity.getName(),
            SharedEnumProductType.valueOf(entity.getProductType()),
            category,
            basePrice
        );
        
        // Override the generated productId with the persisted one
        try {
            java.lang.reflect.Field idField = DomainEntityProduct.class.getDeclaredField("productId");
            idField.setAccessible(true);
            idField.set(product, new DomainValueProductId(entity.getProductId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set product ID", e);
        }
        
        // Set description using the proper method
        if (entity.getDescription() != null && !entity.getDescription().trim().isEmpty()) {
            product.updateDetails(entity.getName(), entity.getDescription());
        }
        
        // Set nutritional info using the proper method
        DomainValueNutritionalInfo nutritionalInfo = mapNutritionalInfo(entity);
        if (nutritionalInfo != null) {
            product.setNutritionalInfo(nutritionalInfo);
        }
        
        // Handle active status and timestamps - need reflection for these as they don't have public setters
        try {
            java.lang.reflect.Field activeField = DomainEntityProduct.class.getDeclaredField("isActive");
            activeField.setAccessible(true);
            activeField.set(product, entity.getIsActive());
            
            java.lang.reflect.Field createdAtField = DomainEntityProduct.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(product, entity.getCreatedAt());
            
            java.lang.reflect.Field lastModifiedField = DomainEntityProduct.class.getDeclaredField("lastModified");
            lastModifiedField.setAccessible(true);
            lastModifiedField.set(product, entity.getLastModified());
        } catch (Exception e) {
            // If we can't set these fields, the entity will use default values
            // This is acceptable as the core product data is set correctly
        }
        
        return product;
    }
    
    /**
     * Backwards compatibility method - creates a minimal category if not provided.
     * This should be avoided in favor of the method that takes a pre-loaded category.
     */
    public DomainEntityProduct toDomain(InfrastructureEntityProduct entity) {
        if (entity == null) {
            return null;
        }
        
        // Create a minimal category - this is not ideal but needed for backwards compatibility
        DomainEntityCategory category = createMinimalCategory(new DomainValueCategoryId(entity.getCategoryId()));
        return toDomain(entity, category);
    }

    public InfrastructureEntityProduct toEntity(DomainEntityProduct domain) {
        if (domain == null) {
            return null;
        }
        
        InfrastructureEntityProduct entity = new InfrastructureEntityProduct();
        
        // Set all fields using domain getters
        if (domain.getId() != null) {
            entity.setProductId(domain.getId().getValue());
        }
        
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setProductType(domain.getProductType().name());
        
        if (domain.getCategory() != null && domain.getCategory().getId() != null) {
            entity.setCategoryId(domain.getCategory().getId().getValue());
        }
        
        if (domain.getBasePrice() != null) {
            entity.setBasePriceAmount(domain.getBasePrice().getAmount());
            entity.setBasePriceCurrency(domain.getBasePrice().getCurrency().name());
        }
        
        entity.setIsActive(domain.getIsActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setLastModified(domain.getLastModified());
        
        // Handle nutritional info
        if (domain.getNutritionalInfo() != null) {
            mapNutritionalInfoToEntity(domain.getNutritionalInfo(), entity);
        }
        
        return entity;
    }

    public List<DomainEntityProduct> toDomainList(List<InfrastructureEntityProduct> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Converts a list of infrastructure entities to domain entities with pre-loaded categories.
     * This is the preferred method as it doesn't create minimal categories.
     * 
     * @param entities list of infrastructure entities
     * @param categoryMapper function to map category IDs to domain categories
     * @return list of domain entities
     */
    public List<DomainEntityProduct> toDomainList(List<InfrastructureEntityProduct> entities, 
            java.util.function.Function<DomainValueCategoryId, DomainEntityCategory> categoryMapper) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(entity -> {
                    DomainEntityCategory category = categoryMapper.apply(new DomainValueCategoryId(entity.getCategoryId()));
                    return toDomain(entity, category);
                })
                .collect(Collectors.toList());
    }

    public List<InfrastructureEntityProduct> toEntityList(List<DomainEntityProduct> domains) {
        if (domains == null) {
            return new ArrayList<>();
        }
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public DomainValueNutritionalInfo mapNutritionalInfo(InfrastructureEntityProduct entity) {
        if (entity == null) {
            return null;
        }
        
        List<String> allergens = new ArrayList<>();
        if (entity.getNutritionalAllergens() != null && !entity.getNutritionalAllergens().trim().isEmpty()) {
            allergens = Arrays.asList(entity.getNutritionalAllergens().split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        }
            
        return new DomainValueNutritionalInfo(
            entity.getNutritionalCalories() != null ? entity.getNutritionalCalories() : 0,
            entity.getNutritionalTotalFat() != null ? entity.getNutritionalTotalFat() : BigDecimal.ZERO,
            entity.getNutritionalCaffeine() != null ? entity.getNutritionalCaffeine() : BigDecimal.ZERO,
            allergens
        );
    }

    public void mapNutritionalInfoToEntity(DomainValueNutritionalInfo nutritionalInfo, InfrastructureEntityProduct entity) {
        if (nutritionalInfo != null && entity != null) {
            entity.setNutritionalCalories(nutritionalInfo.getCalories());
            entity.setNutritionalTotalFat(nutritionalInfo.getTotalFat());
            entity.setNutritionalCaffeine(nutritionalInfo.getCaffeine());
            entity.setNutritionalAllergens(
                nutritionalInfo.getAllergens() != null && !nutritionalInfo.getAllergens().isEmpty() ? 
                String.join(",", nutritionalInfo.getAllergens()) : 
                null
            );
        }
    }
    
    private DomainEntityCategory createMinimalCategory(DomainValueCategoryId categoryId) {
        // Create a minimal category with just the ID - this is a fallback and should be avoided
        try {
            DomainEntityCategory category = new DomainEntityCategory("Unknown Category", 0);
            
            // Set the category ID using reflection
            java.lang.reflect.Field idField = DomainEntityCategory.class.getDeclaredField("categoryId");
            idField.setAccessible(true);
            idField.set(category, categoryId);
            
            return category;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create minimal category", e);
        }
    }
}