package ai.shreds.product_management_availability_shred_g5ds.application.services;

import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOCreateVariant;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOUpdateVariant;
import ai.shreds.product_management_availability_shred_g5ds.application.ports.ApplicationInputPortVariantManagement;
import ai.shreds.product_management_availability_shred_g5ds.application.ports.ApplicationOutputPortEventPublisher;
import ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionVariantNotFound;
import ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionProductNotFound;
import ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionBusinessRule;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainInputPortVariantManagement;
import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityVariant;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueVariantId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueNutritionalInfo;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionProductNotFound;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionBusinessRuleViolation;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionInvalidPrice;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedVariantDTO;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueNutritionalInfo;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumCurrency;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationServiceVariant implements ApplicationInputPortVariantManagement {
    
    private final DomainInputPortVariantManagement variantInputPort;
    private final ApplicationOutputPortEventPublisher eventPublisher;
    
    public ApplicationServiceVariant(DomainInputPortVariantManagement variantInputPort,
                                   ApplicationOutputPortEventPublisher eventPublisher) {
        this.variantInputPort = variantInputPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public SharedVariantDTO createVariant(UUID productId, ApplicationDTOCreateVariant request) {
        try {
            DomainValueProductId domainProductId = DomainValueProductId.fromString(productId.toString());
            DomainValueMoney priceModifier = new DomainValueMoney(
                request.getPriceModifier().getAmount(),
                SharedEnumCurrency.valueOf(request.getPriceModifier().getCurrency())
            );
            
            // Create variant through domain layer
            DomainEntityVariant createdVariant = variantInputPort.createVariant(
                domainProductId,
                request.getSizeName(),
                priceModifier
            );
            
            // Update additional fields if provided
            if (request.getVolumeInOz() != null || request.getVolumeInMl() != null || 
                request.getSizeAbbreviation() != null) {
                createdVariant.updateSize(
                    createdVariant.getSizeName(),
                    request.getVolumeInOz(),
                    request.getVolumeInMl()
                );
                
                if (request.getSizeAbbreviation() != null) {
                    createdVariant.setSizeAbbreviation(request.getSizeAbbreviation());
                }
            }
            
            // Update nutritional info if provided
            if (request.getNutritionalInfo() != null) {
                DomainValueNutritionalInfo nutritionalInfo = new DomainValueNutritionalInfo(
                    request.getNutritionalInfo().getCalories(),
                    request.getNutritionalInfo().getTotalFat(),
                    request.getNutritionalInfo().getCaffeine(),
                    request.getNutritionalInfo().getAllergens()
                );
                createdVariant.updateNutritionalInfo(nutritionalInfo);
            }
            
            return mapDomainEntityToSharedDTO(createdVariant);
            
        } catch (DomainExceptionProductNotFound e) {
            throw new ApplicationExceptionProductNotFound(e.getProductId().getValue());
        } catch (DomainExceptionInvalidPrice e) {
            throw new ApplicationExceptionBusinessRule(
                "InvalidPrice", 
                "Invalid price modifier: " + e.getReason()
            );
        } catch (DomainExceptionBusinessRuleViolation e) {
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        }
    }

    @Override
    public SharedVariantDTO updateVariant(UUID productId, UUID variantId, ApplicationDTOUpdateVariant request) {
        try {
            DomainValueProductId domainProductId = DomainValueProductId.fromString(productId.toString());
            DomainValueVariantId domainVariantId = DomainValueVariantId.fromString(variantId.toString());
            
            // Get existing variant for comparison
            List<DomainEntityVariant> productVariants = variantInputPort.getVariantsByProduct(domainProductId);
            DomainEntityVariant existingVariant = productVariants.stream()
                .filter(v -> v.getId().equals(domainVariantId))
                .findFirst()
                .orElseThrow(() -> new ApplicationExceptionVariantNotFound(variantId, productId));
            
            // Update size information
            if (request.getSizeName() != null) {
                existingVariant.updateSize(
                    request.getSizeName(),
                    request.getVolumeInOz() != null ? request.getVolumeInOz() : existingVariant.getVolumeInOz(),
                    request.getVolumeInMl() != null ? request.getVolumeInMl() : existingVariant.getVolumeInMl()
                );
            }
            
            // Update size abbreviation if provided
            if (request.getSizeAbbreviation() != null) {
                existingVariant.setSizeAbbreviation(request.getSizeAbbreviation());
            }
            
            // Update price modifier if provided
            if (request.getPriceModifier() != null) {
                DomainValueMoney newPriceModifier = new DomainValueMoney(
                    request.getPriceModifier().getAmount(),
                    SharedEnumCurrency.valueOf(request.getPriceModifier().getCurrency())
                );
                existingVariant.updatePriceModifier(newPriceModifier);
            }
            
            // Update nutritional info if provided
            if (request.getNutritionalInfo() != null) {
                DomainValueNutritionalInfo nutritionalInfo = new DomainValueNutritionalInfo(
                    request.getNutritionalInfo().getCalories(),
                    request.getNutritionalInfo().getTotalFat(),
                    request.getNutritionalInfo().getCaffeine(),
                    request.getNutritionalInfo().getAllergens()
                );
                existingVariant.updateNutritionalInfo(nutritionalInfo);
            }
            
            // Update availability if provided
            if (request.getIsAvailable() != null) {
                existingVariant.setAvailable(request.getIsAvailable());
            }
            
            return mapDomainEntityToSharedDTO(existingVariant);
            
        } catch (DomainExceptionProductNotFound e) {
            throw new ApplicationExceptionProductNotFound(e.getProductId().getValue());
        } catch (DomainExceptionInvalidPrice e) {
            throw new ApplicationExceptionBusinessRule(
                "InvalidPrice", 
                "Invalid price modifier: " + e.getReason()
            );
        } catch (DomainExceptionBusinessRuleViolation e) {
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        }
    }

    @Override
    public List<SharedVariantDTO> getVariantsByProduct(UUID productId) {
        try {
            DomainValueProductId domainProductId = DomainValueProductId.fromString(productId.toString());
            List<DomainEntityVariant> variants = variantInputPort.getVariantsByProduct(domainProductId);
            
            return variants.stream()
                .map(this::mapDomainEntityToSharedDTO)
                .collect(Collectors.toList());
                
        } catch (DomainExceptionProductNotFound e) {
            throw new ApplicationExceptionProductNotFound(e.getProductId().getValue());
        }
    }

    @Override
    public void deleteVariant(UUID productId, UUID variantId) {
        try {
            DomainValueProductId domainProductId = DomainValueProductId.fromString(productId.toString());
            DomainValueVariantId domainVariantId = DomainValueVariantId.fromString(variantId.toString());
            
            variantInputPort.deleteVariant(domainProductId, domainVariantId);
            
        } catch (DomainExceptionProductNotFound e) {
            throw new ApplicationExceptionProductNotFound(e.getProductId().getValue());
        } catch (DomainExceptionBusinessRuleViolation e) {
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        }
    }
    
    public SharedVariantDTO mapDomainEntityToSharedDTO(DomainEntityVariant variant) {
        SharedValueMoney sharedPriceModifier = null;
        if (variant.getPriceModifier() != null) {
            sharedPriceModifier = new SharedValueMoney(
                variant.getPriceModifier().getAmount(),
                variant.getPriceModifier().getCurrency().getSymbol()
            );
        }
        
        SharedValueNutritionalInfo sharedNutritionalInfo = null;
        if (variant.getNutritionalInfo() != null) {
            sharedNutritionalInfo = new SharedValueNutritionalInfo(
                variant.getNutritionalInfo().getCalories(),
                variant.getNutritionalInfo().getTotalFat(),
                variant.getNutritionalInfo().getCaffeine(),
                variant.getNutritionalInfo().getAllergens()
            );
        }
        
        return new SharedVariantDTO(
            variant.getId().getValue(),
            variant.getProductId().getValue(),
            variant.getSizeName(),
            variant.getVolumeInOz(),
            variant.getVolumeInMl(),
            variant.getSizeAbbreviation(),
            sharedPriceModifier,
            sharedNutritionalInfo,
            variant.isAvailable()
        );
    }
}