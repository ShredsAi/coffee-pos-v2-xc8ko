package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityVariant;
import ai.shreds.product_management_availability_shred_g5ds.domain.services.DomainServiceVariantManagement;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueVariantId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueMoney;

import java.util.List;

/**
 * Domain input port for variant management operations.
 * Acts as a facade for variant-related domain services and provides
 * a clean interface for the application layer.
 */
public class DomainInputPortVariantManagement {
    private final DomainServiceVariantManagement variantService;

    /**
     * Constructs the VariantManagement domain input port.
     * @param variantService the variant domain service
     */
    public DomainInputPortVariantManagement(DomainServiceVariantManagement variantService) {
        if (variantService == null) {
            throw new IllegalArgumentException("VariantService cannot be null");
        }
        
        this.variantService = variantService;
    }

    /**
     * Creates a new variant for a product with validation and business rule enforcement.
     * @param productId the product identifier
     * @param sizeName the size name
     * @param priceModifier the price modifier
     * @return the created variant entity
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if business rules are violated
     */
    public DomainEntityVariant createVariant(DomainValueProductId productId, String sizeName, 
                                             DomainValueMoney priceModifier) {
        return variantService.createVariant(productId, sizeName, priceModifier);
    }

    /**
     * Updates an existing variant with validation.
     * @param productId the product identifier
     * @param variantId the variant identifier
     * @param sizeName the new size name
     * @return the updated variant entity
     * @throws IllegalArgumentException if variant not found or validation fails
     */
    public DomainEntityVariant updateVariant(DomainValueProductId productId, DomainValueVariantId variantId, 
                                             String sizeName) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (variantId == null) {
            throw new IllegalArgumentException("VariantId cannot be null");
        }
        
        // This method would typically get the variant from a repository first
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Variant retrieval for update not implemented - requires repository integration");
    }

    /**
     * Updates an existing variant with full details.
     * @param productId the product identifier
     * @param variantId the variant identifier
     * @param sizeName the new size name
     * @param priceModifier the new price modifier
     * @return the updated variant entity
     * @throws IllegalArgumentException if variant not found or validation fails
     */
    public DomainEntityVariant updateVariantWithPrice(DomainValueProductId productId, DomainValueVariantId variantId,
                                                      String sizeName, DomainValueMoney priceModifier) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (variantId == null) {
            throw new IllegalArgumentException("VariantId cannot be null");
        }
        
        // This method would typically get the variant from a repository first
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Variant retrieval for update not implemented - requires repository integration");
    }

    /**
     * Gets all variants for a product.
     * @param productId the product identifier
     * @return list of variants for the product
     * @throws IllegalArgumentException if product not found
     */
    public List<DomainEntityVariant> getVariantsByProduct(DomainValueProductId productId) {
        return variantService.getVariantsByProduct(productId);
    }

    /**
     * Gets a specific variant by its identifier.
     * @param variantId the variant identifier
     * @return the variant entity
     * @throws IllegalArgumentException if variant not found
     */
    public DomainEntityVariant getVariantById(DomainValueVariantId variantId) {
        if (variantId == null) {
            throw new IllegalArgumentException("VariantId cannot be null");
        }
        
        // This method would typically get the variant from a repository
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Variant retrieval not implemented - requires repository integration");
    }

    /**
     * Deletes a variant with business rule validation.
     * @param productId the product identifier
     * @param variantId the variant identifier to delete
     * @throws IllegalArgumentException if variant not found
     * @throws IllegalStateException if business rules are violated
     */
    public void deleteVariant(DomainValueProductId productId, DomainValueVariantId variantId) {
        variantService.deleteVariant(productId, variantId);
    }

    /**
     * Validates that a product will have at least one variant after deletion.
     * @param productId the product identifier
     * @param variantBeingDeleted the variant being deleted
     * @throws IllegalStateException if minimum variant requirement would be violated
     */
    public void validateMinimumVariantRequirement(DomainValueProductId productId, 
                                                  DomainValueVariantId variantBeingDeleted) {
        variantService.validateMinimumVariantRequirement(productId, variantBeingDeleted);
    }

    /**
     * Validates that the final price (base price + modifier) is non-negative.
     * @param basePrice the product base price
     * @param priceModifier the price modifier
     * @throws IllegalStateException if final price would be negative
     */
    public void validateFinalPriceNonNegative(DomainValueMoney basePrice, DomainValueMoney priceModifier) {
        variantService.validateFinalPriceNonNegative(basePrice, priceModifier);
    }

    /**
     * Validates that a variant's size name is unique within the product.
     * @param productId the product identifier
     * @param sizeName the size name to validate
     * @param excludeVariantId variant ID to exclude from check (for updates)
     * @throws IllegalStateException if size name already exists
     */
    public void validateVariantSizeUniqueness(DomainValueProductId productId, String sizeName, 
                                              DomainValueVariantId excludeVariantId) {
        variantService.validateVariantSizeUniqueness(productId, sizeName, excludeVariantId);
    }

    /**
     * Calculates the final price for a variant given the product's base price.
     * @param productId the product identifier
     * @param variant the variant
     * @return the calculated final price
     * @throws IllegalArgumentException if product not found
     */
    public DomainValueMoney calculateVariantFinalPrice(DomainValueProductId productId, 
                                                       DomainEntityVariant variant) {
        return variantService.calculateVariantFinalPrice(productId, variant);
    }

    /**
     * Sets the availability status of a variant.
     * @param variantId the variant identifier
     * @param isAvailable the availability status
     * @throws IllegalArgumentException if variant not found
     */
    public void setVariantAvailability(DomainValueVariantId variantId, Boolean isAvailable) {
        if (variantId == null) {
            throw new IllegalArgumentException("VariantId cannot be null");
        }
        if (isAvailable == null) {
            throw new IllegalArgumentException("Availability status cannot be null");
        }
        
        // This method would typically get the variant from a repository and update availability
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Variant availability update not implemented - requires repository integration");
    }

    /**
     * Gets all available variants for a product.
     * @param productId the product identifier
     * @return list of available variants
     * @throws IllegalArgumentException if product not found
     */
    public List<DomainEntityVariant> getAvailableVariantsByProduct(DomainValueProductId productId) {
        List<DomainEntityVariant> allVariants = variantService.getVariantsByProduct(productId);
        return allVariants.stream()
                .filter(DomainEntityVariant::isAvailable)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Counts the number of variants for a product.
     * @param productId the product identifier
     * @return the number of variants
     * @throws IllegalArgumentException if product not found
     */
    public int countVariantsByProduct(DomainValueProductId productId) {
        return variantService.getVariantsByProduct(productId).size();
    }

    /**
     * Counts the number of available variants for a product.
     * @param productId the product identifier
     * @return the number of available variants
     * @throws IllegalArgumentException if product not found
     */
    public int countAvailableVariantsByProduct(DomainValueProductId productId) {
        return getAvailableVariantsByProduct(productId).size();
    }
}