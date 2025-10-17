package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
public class InfrastructureEntityProduct {
    
    @Id
    @Column(name = "product_id")
    private UUID productId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "product_type", nullable = false, length = 20)
    private String productType;
    
    @Column(name = "category_id", nullable = false)
    private UUID categoryId;
    
    @Column(name = "base_price_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePriceAmount;
    
    @Column(name = "base_price_currency", nullable = false, length = 3)
    private String basePriceCurrency = "USD";
    
    @Column(name = "nutritional_calories")
    private Integer nutritionalCalories;
    
    @Column(name = "nutritional_total_fat", precision = 5, scale = 2)
    private BigDecimal nutritionalTotalFat;
    
    @Column(name = "nutritional_caffeine", precision = 5, scale = 2)
    private BigDecimal nutritionalCaffeine;
    
    @Column(name = "nutritional_allergens")
    private String nutritionalAllergens;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;
    
    public InfrastructureEntityProduct() {}
    
    @PrePersist
    protected void onCreate() {
        if (productId == null) {
            productId = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        lastModified = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastModified = LocalDateTime.now();
    }
    
    // Getters and setters
    public UUID getProductId() {
        return productId;
    }
    
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    public UUID getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
    
    public BigDecimal getBasePriceAmount() {
        return basePriceAmount;
    }
    
    public void setBasePriceAmount(BigDecimal basePriceAmount) {
        this.basePriceAmount = basePriceAmount;
    }
    
    public String getBasePriceCurrency() {
        return basePriceCurrency;
    }
    
    public void setBasePriceCurrency(String basePriceCurrency) {
        this.basePriceCurrency = basePriceCurrency;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
}