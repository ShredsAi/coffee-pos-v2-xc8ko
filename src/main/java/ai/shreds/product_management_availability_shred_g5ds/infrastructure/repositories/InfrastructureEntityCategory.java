package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "product_categories")
public class InfrastructureEntityCategory {
    
    @Id
    @Column(name = "category_id")
    private UUID categoryId;
    
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "parent_category_id")
    private UUID parentCategoryId;
    
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    public InfrastructureEntityCategory() {}
    
    @PrePersist
    protected void onCreate() {
        if (categoryId == null) {
            categoryId = UUID.randomUUID();
        }
    }
    
    // Getters and setters
    public UUID getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
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
    
    public UUID getParentCategoryId() {
        return parentCategoryId;
    }
    
    public void setParentCategoryId(UUID parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}