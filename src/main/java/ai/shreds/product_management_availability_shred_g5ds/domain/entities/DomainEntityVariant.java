package ai.shreds.product_management_availability_shred_g5ds.domain.entities;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueVariantId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueNutritionalInfo;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain entity representing a product variant with size and pricing information.
 * Variants belong to a specific product and represent different size configurations.
 */
public class DomainEntityVariant {
    private DomainValueVariantId variantId;
    private DomainValueProductId productId;
    private String sizeName;
    private BigDecimal volumeInOz;
    private BigDecimal volumeInMl;
    private String sizeAbbreviation;
    private DomainValueMoney priceModifier;
    private DomainValueNutritionalInfo nutritionalInfo;
    private Boolean isAvailable;

    /**
     * Constructs a new Variant entity.
     * @param productId the ID of the product this variant belongs to
     * @param sizeName the size name (e.g., "Small", "Medium", "Large")
     * @param priceModifier the price modifier (can be positive or negative)
     */
    public DomainEntityVariant(DomainValueProductId productId, String sizeName, 
                              DomainValueMoney priceModifier) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (sizeName == null || sizeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Size name cannot be null or empty");
        }
        if (sizeName.length() > 50) {
            throw new IllegalArgumentException("Size name cannot exceed 50 characters");
        }
        if (priceModifier == null) {
            throw new IllegalArgumentException("Price modifier cannot be null");
        }
        
        this.variantId = DomainValueVariantId.generate();
        this.productId = productId;
        this.sizeName = sizeName.trim();
        this.priceModifier = priceModifier;
        this.isAvailable = true;
    }

    /**
     * Updates the size information for this variant.
     * @param sizeName the new size name
     * @param volumeInOz the volume in ounces
     * @param volumeInMl the volume in milliliters
     */
    public void updateSize(String sizeName, BigDecimal volumeInOz, BigDecimal volumeInMl) {
        if (sizeName != null && !sizeName.trim().isEmpty()) {
            if (sizeName.length() > 50) {
                throw new IllegalArgumentException("Size name cannot exceed 50 characters");
            }
            this.sizeName = sizeName.trim();
        }
        
        if (volumeInOz != null) {
            if (volumeInOz.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Volume in ounces cannot be negative");
            }
            this.volumeInOz = volumeInOz;
        }
        
        if (volumeInMl != null) {
            if (volumeInMl.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Volume in milliliters cannot be negative");
            }
            this.volumeInMl = volumeInMl;
        }
    }

    /**
     * Updates the price modifier for this variant.
     * @param priceModifier the new price modifier
     */
    public void updatePriceModifier(DomainValueMoney priceModifier) {
        if (priceModifier == null) {
            throw new IllegalArgumentException("Price modifier cannot be null");
        }
        
        this.priceModifier = priceModifier;
    }

    /**
     * Updates the nutritional information for this variant.
     * @param nutritionalInfo the new nutritional information
     */
    public void updateNutritionalInfo(DomainValueNutritionalInfo nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }

    /**
     * Sets the availability status of this variant.
     * @param isAvailable the availability status
     */
    public void setAvailable(Boolean isAvailable) {
        if (isAvailable == null) {
            throw new IllegalArgumentException("Availability status cannot be null");
        }
        
        this.isAvailable = isAvailable;
    }

    /**
     * Calculates the final price for this variant given a base price.
     * @param basePrice the base price of the product
     * @return the final price after applying the price modifier
     */
    public DomainValueMoney calculateFinalPrice(DomainValueMoney basePrice) {
        if (basePrice == null) {
            throw new IllegalArgumentException("Base price cannot be null");
        }
        
        DomainValueMoney finalPrice = basePrice.add(priceModifier);
        
        if (finalPrice.isNegative()) {
            throw new IllegalStateException(
                String.format("Final price cannot be negative. Base: %s, Modifier: %s", 
                        basePrice, priceModifier));
        }
        
        return finalPrice;
    }

    /**
     * Validates business rules for this variant.
     * @throws IllegalStateException if business rules are violated
     */
    public void validateBusinessRules() {
        if (sizeName == null || sizeName.trim().isEmpty()) {
            throw new IllegalStateException("Variant must have a size name");
        }
        if (priceModifier == null) {
            throw new IllegalStateException("Variant must have a price modifier");
        }
    }

    /**
     * Sets the size abbreviation.
     * @param sizeAbbreviation the size abbreviation (e.g., "S", "M", "L")
     */
    public void setSizeAbbreviation(String sizeAbbreviation) {
        if (sizeAbbreviation != null && sizeAbbreviation.length() > 5) {
            throw new IllegalArgumentException("Size abbreviation cannot exceed 5 characters");
        }
        
        this.sizeAbbreviation = sizeAbbreviation;
    }

    // Getters
    public DomainValueVariantId getId() {
        return variantId;
    }

    public DomainValueProductId getProductId() {
        return productId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public BigDecimal getVolumeInOz() {
        return volumeInOz;
    }

    public BigDecimal getVolumeInMl() {
        return volumeInMl;
    }

    public String getSizeAbbreviation() {
        return sizeAbbreviation;
    }

    public DomainValueMoney getPriceModifier() {
        return priceModifier;
    }

    public DomainValueNutritionalInfo getNutritionalInfo() {
        return nutritionalInfo;
    }

    public Boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainEntityVariant that = (DomainEntityVariant) other;
        return Objects.equals(variantId, that.variantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variantId);
    }

    @Override
    public String toString() {
        return String.format("DomainEntityVariant{variantId=%s, productId=%s, sizeName='%s', isAvailable=%s}",
            variantId, productId, sizeName, isAvailable);
    }
}