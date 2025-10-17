package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityVariant;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.*;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumCurrency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InfrastructureMapperVariant {

    public DomainEntityVariant toDomain(InfrastructureEntityVariant entity) {
        if (entity == null) {
            return null;
        }
        
        // Create value objects
        DomainValueProductId productId = new DomainValueProductId(entity.getProductId());
        DomainValueMoney priceModifier = mapPriceModifier(entity);
        
        // Create domain entity using proper constructor
        DomainEntityVariant variant = new DomainEntityVariant(
            productId,
            entity.getSizeName(),
            priceModifier
        );
        
        // Override the generated variantId with the persisted one
        try {
            java.lang.reflect.Field idField = DomainEntityVariant.class.getDeclaredField("variantId");
            idField.setAccessible(true);
            idField.set(variant, new DomainValueVariantId(entity.getVariantId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set variant ID", e);
        }
        
        // Set size information using the proper method
        if (entity.getVolumeInOz() != null || entity.getVolumeInMl() != null) {
            variant.updateSize(
                entity.getSizeName(), // Keep the same name
                entity.getVolumeInOz(),
                entity.getVolumeInMl()
            );
        }
        
        // Set size abbreviation using the proper method
        if (entity.getSizeAbbreviation() != null) {
            variant.setSizeAbbreviation(entity.getSizeAbbreviation());
        }
        
        // Set availability using the proper method
        if (entity.getIsAvailable() != null) {
            variant.setAvailable(entity.getIsAvailable());
        }
        
        // Set nutritional info using the proper method
        DomainValueNutritionalInfo nutritionalInfo = mapNutritionalInfo(entity);
        if (nutritionalInfo != null) {
            variant.updateNutritionalInfo(nutritionalInfo);
        }
        
        return variant;
    }

    public InfrastructureEntityVariant toEntity(DomainEntityVariant domain) {
        if (domain == null) {
            return null;
        }
        
        InfrastructureEntityVariant entity = new InfrastructureEntityVariant();
        
        // Set all fields using domain getters
        if (domain.getId() != null) {
            entity.setVariantId(domain.getId().getValue());
        }
        
        if (domain.getProductId() != null) {
            entity.setProductId(domain.getProductId().getValue());
        }
        
        entity.setSizeName(domain.getSizeName());
        entity.setVolumeInOz(domain.getVolumeInOz());
        entity.setVolumeInMl(domain.getVolumeInMl());
        entity.setSizeAbbreviation(domain.getSizeAbbreviation());
        entity.setIsAvailable(domain.isAvailable());
        
        // Map price modifier
        mapPriceModifierToEntity(domain.getPriceModifier(), entity);
        
        // Map nutritional info if available
        if (domain.getNutritionalInfo() != null) {
            DomainValueNutritionalInfo nutritionalInfo = domain.getNutritionalInfo();
            entity.setNutritionalCalories(nutritionalInfo.getCalories());
            entity.setNutritionalTotalFat(nutritionalInfo.getTotalFat());
            entity.setNutritionalCaffeine(nutritionalInfo.getCaffeine());
            entity.setNutritionalAllergens(
                nutritionalInfo.getAllergens() != null && !nutritionalInfo.getAllergens().isEmpty() ? 
                String.join(",", nutritionalInfo.getAllergens()) : 
                null
            );
        }
        
        return entity;
    }

    public List<DomainEntityVariant> toDomainList(List<InfrastructureEntityVariant> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<InfrastructureEntityVariant> toEntityList(List<DomainEntityVariant> domains) {
        if (domains == null) {
            return new ArrayList<>();
        }
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public DomainValueMoney mapPriceModifier(InfrastructureEntityVariant entity) {
        if (entity == null || entity.getPriceModifierAmount() == null) {
            return new DomainValueMoney(BigDecimal.ZERO, SharedEnumCurrency.USD);
        }
        
        try {
            SharedEnumCurrency currency = SharedEnumCurrency.valueOf(
                entity.getPriceModifierCurrency() != null ? entity.getPriceModifierCurrency() : "USD"
            );
            return new DomainValueMoney(entity.getPriceModifierAmount(), currency);
        } catch (IllegalArgumentException e) {
            // If currency is invalid, use USD as default
            return new DomainValueMoney(entity.getPriceModifierAmount(), SharedEnumCurrency.USD);
        }
    }

    public void mapPriceModifierToEntity(DomainValueMoney priceModifier, InfrastructureEntityVariant entity) {
        if (priceModifier != null && entity != null) {
            entity.setPriceModifierAmount(priceModifier.getAmount());
            entity.setPriceModifierCurrency(priceModifier.getCurrency().name());
        } else if (entity != null) {
            // Set defaults
            entity.setPriceModifierAmount(BigDecimal.ZERO);
            entity.setPriceModifierCurrency("USD");
        }
    }
    
    private DomainValueNutritionalInfo mapNutritionalInfo(InfrastructureEntityVariant entity) {
        if (entity == null) {
            return null;
        }
        
        // Check if we have any nutritional data
        if (entity.getNutritionalCalories() == null && 
            entity.getNutritionalTotalFat() == null && 
            entity.getNutritionalCaffeine() == null && 
            (entity.getNutritionalAllergens() == null || entity.getNutritionalAllergens().trim().isEmpty())) {
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
}