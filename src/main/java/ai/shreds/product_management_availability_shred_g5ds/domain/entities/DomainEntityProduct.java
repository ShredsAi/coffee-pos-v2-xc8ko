package ai.shreds.product_management_availability_shred_g5ds.domain.entities;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueNutritionalInfo;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueVariantId;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumProductType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Domain entity representing a product in the coffee shop system.
 * Aggregate root that manages product lifecycle and business rules.
 */
public class DomainEntityProduct {
    private DomainValueProductId productId;
    private String name;
    private String description;
    private SharedEnumProductType productType;
    private DomainEntityCategory category;
    private DomainValueMoney basePrice;
    private DomainValueNutritionalInfo nutritionalInfo;
    private List<DomainEntityVariant> variants;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;

    /**
     * Constructs a new Product entity.
     * @param name the product name
     * @param productType the type of product
     * @param category the product category
     * @param basePrice the base price
     */
    public DomainEntityProduct(String name, SharedEnumProductType productType, 
                              DomainEntityCategory category, DomainValueMoney basePrice) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Product name cannot exceed 100 characters");
        }
        if (productType == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (basePrice == null) {
            throw new IllegalArgumentException("Base price cannot be null");
        }
        if (!basePrice.isPositive()) {
            throw new IllegalArgumentException("Base price must be positive");
        }
        
        this.productId = DomainValueProductId.generate();
        this.name = name.trim();
        this.productType = productType;
        this.category = category;
        this.basePrice = basePrice;
        this.variants = new ArrayList<>();
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Updates product details.
     * @param name the new name
     * @param description the new description
     */
    public void updateDetails(String name, String description) {
        if (name != null && !name.trim().isEmpty()) {
            if (name.length() > 100) {
                throw new IllegalArgumentException("Product name cannot exceed 100 characters");
            }
            this.name = name.trim();
        }
        if (description != null) {
            if (description.length() > 500) {
                throw new IllegalArgumentException("Product description cannot exceed 500 characters");
            }
            this.description = description.trim();
        }
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Updates the product price.
     * @param newPrice the new base price
     */
    public void updatePrice(DomainValueMoney newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("New price cannot be null");
        }
        if (!newPrice.isPositive()) {
            throw new IllegalArgumentException("New price must be positive");
        }
        
        this.basePrice = newPrice;
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Adds a variant to the product.
     * @param variant the variant to add
     */
    public void addVariant(DomainEntityVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("Variant cannot be null");
        }
        if (!variant.getProductId().equals(this.productId)) {
            throw new IllegalArgumentException("Variant must belong to this product");
        }
        
        this.variants.add(variant);
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Removes a variant from the product.
     * @param variantId the ID of the variant to remove
     */
    public void removeVariant(DomainValueVariantId variantId) {
        if (variantId == null) {
            throw new IllegalArgumentException("VariantId cannot be null");
        }
        
        // Ensure at least one variant remains if product is active
        if (isActive && variants.size() <= 1) {
            throw new IllegalStateException("Active products must have at least one variant");
        }
        
        variants.removeIf(variant -> variant.getId().equals(variantId));
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Deactivates the product.
     */
    public void deactivate() {
        this.isActive = false;
        // Deactivate all variants when product is deactivated
        variants.forEach(variant -> variant.setAvailable(false));
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Activates the product.
     */
    public void activate() {
        validateBusinessRules();
        this.isActive = true;
        this.lastModified = LocalDateTime.now();
    }

    /**
     * Validates business rules for the product.
     * @throws IllegalStateException if business rules are violated
     */
    public void validateBusinessRules() {
        if (!basePrice.isPositive()) {
            throw new IllegalStateException("Active products must have a positive base price");
        }
        if (variants.isEmpty()) {
            throw new IllegalStateException("Active products must have at least one variant");
        }
        if (variants.stream().noneMatch(DomainEntityVariant::isAvailable)) {
            throw new IllegalStateException("Active products must have at least one available variant");
        }
    }

    /**
     * Gets active variants for this product.
     * @return list of active variants
     */
    public List<DomainEntityVariant> getActiveVariants() {
        return variants.stream()
                .filter(DomainEntityVariant::isAvailable)
                .collect(Collectors.toList());
    }

    // Getters
    public DomainValueProductId getId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public SharedEnumProductType getProductType() {
        return productType;
    }

    public DomainEntityCategory getCategory() {
        return category;
    }

    public DomainValueMoney getBasePrice() {
        return basePrice;
    }

    public DomainValueNutritionalInfo getNutritionalInfo() {
        return nutritionalInfo;
    }

    public void setNutritionalInfo(DomainValueNutritionalInfo nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
        this.lastModified = LocalDateTime.now();
    }

    public List<DomainEntityVariant> getVariants() {
        return new ArrayList<>(variants);
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainEntityProduct that = (DomainEntityProduct) other;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return String.format("DomainEntityProduct{productId=%s, name='%s', productType=%s, isActive=%s}",
            productId, name, productType, isActive);
    }
}