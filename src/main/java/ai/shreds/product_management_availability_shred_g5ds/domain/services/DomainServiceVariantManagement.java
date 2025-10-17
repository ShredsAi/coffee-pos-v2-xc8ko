package ai.shreds.product_management_availability_shred_g5ds.domain.services;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityVariant;
import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityProduct;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueVariantId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortVariantRepository;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortProductRepository;

import java.util.List;

/**
 * Domain service responsible for variant management operations.
 * Handles variant lifecycle, business rule validation, and price calculations.
 */
public class DomainServiceVariantManagement {
    private final DomainOutputPortVariantRepository variantRepository;
    private final DomainOutputPortProductRepository productRepository;

    /**
     * Constructs the VariantManagement domain service.
     * @param variantRepository the variant repository port
     * @param productRepository the product repository port
     */
    public DomainServiceVariantManagement(DomainOutputPortVariantRepository variantRepository,
                                         DomainOutputPortProductRepository productRepository) {
        if (variantRepository == null) {
            throw new IllegalArgumentException("VariantRepository cannot be null");
        }
        if (productRepository == null) {
            throw new IllegalArgumentException("ProductRepository cannot be null");
        }
        
        this.variantRepository = variantRepository;
        this.productRepository = productRepository;
    }

    /**
     * Creates a new variant for a product with validation.
     * @param productId the product identifier
     * @param sizeName the size name
     * @param priceModifier the price modifier
     * @return the created variant entity
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if business rules are violated
     */
    public DomainEntityVariant createVariant(DomainValueProductId productId, String sizeName, 
                                             DomainValueMoney priceModifier) {
        // Validate product exists
        DomainEntityProduct product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found");
        }
        
        // Validate final price will be non-negative
        validateFinalPriceNonNegative(product.getBasePrice(), priceModifier);
        
        // Create variant entity
        DomainEntityVariant variant = new DomainEntityVariant(productId, sizeName, priceModifier);
        
        // Validate business rules
        variant.validateBusinessRules();
        
        // Save variant
        return variantRepository.save(variant);
    }

    /**
     * Updates an existing variant with validation.
     * @param variant the variant to update
     * @param sizeName the new size name
     * @param priceModifier the new price modifier
     * @return the updated variant entity
     * @throws IllegalArgumentException if validation fails
     */
    public DomainEntityVariant updateVariant(DomainEntityVariant variant, String sizeName, 
                                             DomainValueMoney priceModifier) {
        if (variant == null) {
            throw new IllegalArgumentException("Variant cannot be null");
        }
        
        // Get product for price validation
        DomainEntityProduct product = productRepository.findById(variant.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + variant.getProductId() + " not found");
        }
        
        // Update size name if provided
        if (sizeName != null && !sizeName.trim().isEmpty()) {
            variant.updateSize(sizeName, variant.getVolumeInOz(), variant.getVolumeInMl());
        }
        
        // Update price modifier if provided
        if (priceModifier != null) {
            // Validate final price will be non-negative
            validateFinalPriceNonNegative(product.getBasePrice(), priceModifier);
            variant.updatePriceModifier(priceModifier);
        }
        
        // Validate business rules
        variant.validateBusinessRules();
        
        // Save updated variant
        return variantRepository.save(variant);
    }

    /**
     * Deletes a variant with business rule validation.
     * @param productId the product identifier
     * @param variantId the variant identifier to delete
     * @throws IllegalArgumentException if variant not found
     * @throws IllegalStateException if business rules are violated
     */
    public void deleteVariant(DomainValueProductId productId, DomainValueVariantId variantId) {
        // Validate variant exists
        DomainEntityVariant variant = variantRepository.findById(variantId);
        if (variant == null) {
            throw new IllegalArgumentException("Variant with ID " + variantId + " not found");
        }
        
        // Validate variant belongs to the specified product
        if (!variant.getProductId().equals(productId)) {
            throw new IllegalArgumentException(
                String.format("Variant %s does not belong to product %s", variantId, productId));
        }
        
        // Validate minimum variant requirement
        validateMinimumVariantRequirement(productId, variantId);
        
        // Delete variant
        variantRepository.delete(variantId);
    }

    /**
     * Validates that a product will have at least one variant after deletion.
     * @param productId the product identifier
     * @param variantBeingDeleted the variant being deleted
     * @throws IllegalStateException if minimum variant requirement would be violated
     */
    public void validateMinimumVariantRequirement(DomainValueProductId productId, 
                                                  DomainValueVariantId variantBeingDeleted) {
        // Get product to check if it's active
        DomainEntityProduct product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found");
        }
        
        // Only active products need minimum variant requirement
        if (!product.getIsActive()) {
            return; // Inactive products can have no variants
        }
        
        // Count current variants for the product
        Integer currentVariantCount = variantRepository.countByProductId(productId);
        
        // Check if deleting this variant would leave the product with no variants
        if (currentVariantCount != null && currentVariantCount <= 1) {
            throw new IllegalStateException(
                String.format("Cannot delete variant %s: active product %s must have at least one variant", 
                        variantBeingDeleted, productId));
        }
    }

    /**
     * Validates that the final price (base price + modifier) is non-negative.
     * @param basePrice the product base price
     * @param priceModifier the price modifier
     * @throws IllegalStateException if final price would be negative
     */
    public void validateFinalPriceNonNegative(DomainValueMoney basePrice, DomainValueMoney priceModifier) {
        if (basePrice == null) {
            throw new IllegalArgumentException("Base price cannot be null");
        }
        if (priceModifier == null) {
            throw new IllegalArgumentException("Price modifier cannot be null");
        }
        
        try {
            DomainValueMoney finalPrice = basePrice.add(priceModifier);
            if (finalPrice.isNegative()) {
                throw new IllegalStateException(
                    String.format("Final price cannot be negative. Base: %s, Modifier: %s, Result: %s", 
                            basePrice, priceModifier, finalPrice));
            }
        } catch (IllegalArgumentException e) {
            // Currency mismatch or other validation error
            throw new IllegalStateException("Price calculation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all variants for a product with business rule validation.
     * @param productId the product identifier
     * @return list of variants for the product
     * @throws IllegalArgumentException if product not found
     */
    public List<DomainEntityVariant> getVariantsByProduct(DomainValueProductId productId) {
        // Validate product exists
        DomainEntityProduct product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found");
        }
        
        return variantRepository.findByProductId(productId);
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
        List<DomainEntityVariant> existingVariants = variantRepository.findByProductId(productId);
        
        for (DomainEntityVariant variant : existingVariants) {
            // Skip the variant being updated
            if (excludeVariantId != null && variant.getId().equals(excludeVariantId)) {
                continue;
            }
            
            if (sizeName.equalsIgnoreCase(variant.getSizeName())) {
                throw new IllegalStateException(
                    String.format("Size name '%s' already exists for product %s", sizeName, productId));
            }
        }
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
        DomainEntityProduct product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found");
        }
        
        return variant.calculateFinalPrice(product.getBasePrice());
    }
}