package ai.shreds.product_management_availability_shred_g5ds.domain.entities;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;

import java.util.Objects;

/**
 * Domain entity representing a product category in the coffee shop system.
 * Supports hierarchical organization with parent-child relationships.
 */
public class DomainEntityCategory {
    private DomainValueCategoryId categoryId;
    private String name;
    private String description;
    private DomainEntityCategory parentCategory;
    private Integer displayOrder;
    private Boolean isActive;

    /**
     * Constructs a new Category entity.
     * @param name the category name
     * @param displayOrder the display order
     */
    public DomainEntityCategory(String name, Integer displayOrder) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Category name cannot exceed 100 characters");
        }
        if (displayOrder == null || displayOrder < 0) {
            throw new IllegalArgumentException("Display order must be non-negative");
        }
        
        this.categoryId = DomainValueCategoryId.generate();
        this.name = name.trim();
        this.displayOrder = displayOrder;
        this.isActive = true;
    }

    /**
     * Updates the category name.
     * @param name the new name
     */
    public void updateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Category name cannot exceed 100 characters");
        }
        
        this.name = name.trim();
    }

    /**
     * Updates the category description.
     * @param description the new description
     */
    public void updateDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Category description cannot exceed 500 characters");
        }
        
        this.description = description != null ? description.trim() : null;
    }

    /**
     * Sets the parent category.
     * @param parentCategory the parent category
     */
    public void setParentCategory(DomainEntityCategory parentCategory) {
        if (parentCategory != null) {
            validateNoCircularReference(parentCategory);
        }
        
        this.parentCategory = parentCategory;
    }

    /**
     * Updates the display order.
     * @param displayOrder the new display order
     */
    public void updateDisplayOrder(Integer displayOrder) {
        if (displayOrder == null || displayOrder < 0) {
            throw new IllegalArgumentException("Display order must be non-negative");
        }
        
        this.displayOrder = displayOrder;
    }

    /**
     * Deactivates the category.
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Activates the category.
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Validates that setting a new parent won't create a circular reference.
     * @param newParent the proposed parent category
     * @throws IllegalArgumentException if a circular reference would be created
     */
    public void validateNoCircularReference(DomainEntityCategory newParent) {
        if (newParent == null) {
            return;
        }
        
        // Check if the new parent is this category itself
        if (newParent.getId().equals(this.categoryId)) {
            throw new IllegalArgumentException("Category cannot be its own parent");
        }
        
        // Check if this category is already an ancestor of the new parent
        DomainEntityCategory current = newParent.getParentCategory();
        while (current != null) {
            if (current.getId().equals(this.categoryId)) {
                throw new IllegalArgumentException(
                    String.format("Circular reference detected: Category %s is already an ancestor of %s", 
                            this.categoryId, newParent.getId()));
            }
            current = current.getParentCategory();
        }
    }

    /**
     * Checks if this is a root category (no parent).
     * @return true if this category has no parent
     */
    public boolean isRootCategory() {
        return parentCategory == null;
    }

    /**
     * Gets the full hierarchical path of this category.
     * @return the category path as a string
     */
    public String getFullPath() {
        if (parentCategory == null) {
            return name;
        }
        return parentCategory.getFullPath() + " > " + name;
    }

    // Getters
    public DomainValueCategoryId getId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public DomainEntityCategory getParentCategory() {
        return parentCategory;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainEntityCategory that = (DomainEntityCategory) other;
        return Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }

    @Override
    public String toString() {
        return String.format("DomainEntityCategory{categoryId=%s, name='%s', displayOrder=%d, isActive=%s}",
            categoryId, name, displayOrder, isActive);
    }
}