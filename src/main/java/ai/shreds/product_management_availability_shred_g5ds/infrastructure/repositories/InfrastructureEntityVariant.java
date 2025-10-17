package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
public class InfrastructureEntityVariant {
    
    @Id
    @Column(name = "variant_id")
    private UUID variantId;
    
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    
    @Column(name = "size_name", nullable = false, length = 50)
    private String sizeName;
    
    @Column(name = "volume_in_oz", precision = 5, scale = 2)
    private BigDecimal volumeInOz;
    
    @Column(name = "volume_in_ml", precision = 7, scale = 2)
    private BigDecimal volumeInMl;
    
    @Column(name = "size_abbreviation", length = 5)
    private String sizeAbbreviation;
    
    @Column(name = "price_modifier_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceModifierAmount = BigDecimal.ZERO;
    
    @Column(name = "price_modifier_currency", nullable = false, length = 3)
    private String priceModifierCurrency = "USD";
    
    @Column(name = "nutritional_calories")
    private Integer nutritionalCalories;
    
    @Column(name = "nutritional_total_fat", precision = 5, scale = 2)
    private BigDecimal nutritionalTotalFat;
    
    @Column(name = "nutritional_caffeine", precision = 5, scale = 2)
    private BigDecimal nutritionalCaffeine;
    
    @Column(name = "nutritional_allergens")
    private String nutritionalAllergens;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    public InfrastructureEntityVariant() {}
    
    @PrePersist
    protected void onCreate() {
        if (variantId == null) {
            variantId = UUID.randomUUID();
        }
    }
    
    // Getters and setters
    public UUID getVariantId() {
        return variantId;
    }
    
    public void setVariantId(UUID variantId) {
        this.variantId = variantId;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    
    public String getSizeName() {
        return sizeName;
    }
    
    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }
    
    public BigDecimal getVolumeInOz() {
        return volumeInOz;
    }
    
    public void setVolumeInOz(BigDecimal volumeInOz) {
        this.volumeInOz = volumeInOz;
    }
    
    public BigDecimal getVolumeInMl() {
        return volumeInMl;
    }
    
    public void setVolumeInMl(BigDecimal volumeInMl) {
        this.volumeInMl = volumeInMl;
    }
    
    public String getSizeAbbreviation() {
        return sizeAbbreviation;
    }
    
    public void setSizeAbbreviation(String sizeAbbreviation) {
        this.sizeAbbreviation = sizeAbbreviation;
    }
    
    public BigDecimal getPriceModifierAmount() {
        return priceModifierAmount;
    }
    
    public void setPriceModifierAmount(BigDecimal priceModifierAmount) {
        this.priceModifierAmount = priceModifierAmount;
    }
    
    public String getPriceModifierCurrency() {
        return priceModifierCurrency;
    }
    
    public void setPriceModifierCurrency(String priceModifierCurrency) {
        this.priceModifierCurrency = priceModifierCurrency;
    }
    
    public Integer getNutritionalCalories() {
        return nutritionalCalories;
    }
    
    public void setNutritionalCalories(Integer nutritionalCalories) {
        this.nutritionalCalories = nutritionalCalories;
    }
    
    public BigDecimal getNutritionalTotalFat() {
        return nutritionalTotalFat;
    }
    
    public void setNutritionalTotalFat(BigDecimal nutritionalTotalFat) {
        this.nutritionalTotalFat = nutritionalTotalFat;
    }
    
    public BigDecimal getNutritionalCaffeine() {
        return nutritionalCaffeine;
    }
    
    public void setNutritionalCaffeine(BigDecimal nutritionalCaffeine) {
        this.nutritionalCaffeine = nutritionalCaffeine;
    }
    
    public String getNutritionalAllergens() {
        return nutritionalAllergens;
    }
    
    public void setNutritionalAllergens(String nutritionalAllergens) {
        this.nutritionalAllergens = nutritionalAllergens;
    }
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}