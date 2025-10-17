package ai.shreds.product_management_availability_shred_g5ds.application;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

public class ApplicationDTOCreateCategory {
    
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Category description cannot exceed 500 characters")
    private String description;
    
    private UUID parentCategoryId;
    
    @NotNull(message = "Display order is required")
    private Integer displayOrder;

    // Default constructor
    public ApplicationDTOCreateCategory() {}

    // Constructor with required fields
    public ApplicationDTOCreateCategory(String name, Integer displayOrder) {
        this.name = name;
        this.displayOrder = displayOrder;
    }

    // Full constructor
    public ApplicationDTOCreateCategory(String name, String description, UUID parentCategoryId, Integer displayOrder) {
        this.name = name;
        this.description = description;
        this.parentCategoryId = parentCategoryId;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
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
}