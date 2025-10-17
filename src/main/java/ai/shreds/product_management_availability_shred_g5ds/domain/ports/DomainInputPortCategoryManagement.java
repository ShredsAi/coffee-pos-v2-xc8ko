package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityCategory;
import ai.shreds.product_management_availability_shred_g5ds.domain.services.DomainServiceCategoryManagement;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;

import java.util.List;

/**
 * Domain input port for category management operations.
 * Acts as a facade for category-related domain services and provides
 * a clean interface for the application layer.
 */
public class DomainInputPortCategoryManagement {
    private final DomainServiceCategoryManagement categoryService;

    /**
     * Constructs the CategoryManagement domain input port.
     * @param categoryService the category domain service
     */
    public DomainInputPortCategoryManagement(DomainServiceCategoryManagement categoryService) {
        if (categoryService == null) {
            throw new IllegalArgumentException("CategoryService cannot be null");
        }
        
        this.categoryService = categoryService;
    }

    /**
     * Creates a new category with validation and business rule enforcement.
     * @param name the category name
     * @param displayOrder the display order
     * @param parentCategoryId the parent category identifier (optional)
     * @return the created category entity
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if business rules are violated
     */
    public DomainEntityCategory createCategory(String name, Integer displayOrder, 
                                              DomainValueCategoryId parentCategoryId) {
        return categoryService.createCategory(name, displayOrder, parentCategoryId);
    }

    /**
     * Updates an existing category with validation.
     * @param categoryId the category identifier
     * @param name the new name
     * @param description the new description
     * @return the updated category entity
     * @throws IllegalArgumentException if category not found or validation fails
     */
    public DomainEntityCategory updateCategory(DomainValueCategoryId categoryId, String name, String description) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId cannot be null");
        }
        
        // This method would typically get the category from a repository
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Category retrieval for update not implemented - requires repository integration");
    }

    /**
     * Gets a category by its identifier.
     * @param categoryId the category identifier
     * @return the category entity
     * @throws IllegalArgumentException if category not found
     */
    public DomainEntityCategory getCategoryById(DomainValueCategoryId categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId cannot be null");
        }
        
        // This method would typically get the category from a repository
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Category retrieval not implemented - requires repository integration");
    }

    /**
     * Lists categories with optional filtering by active status.
     * @param active the active status filter (optional)
     * @return list of categories
     */
    public List<DomainEntityCategory> listCategories(Boolean active) {
        // This method would typically list categories from a repository
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Category listing not implemented - requires repository integration");
    }

    /**
     * Gets the category hierarchy as a tree structure.
     * @return list of root categories with their hierarchical structure
     */
    public List<DomainEntityCategory> getCategoryHierarchy() {
        // This method would typically get all categories and build hierarchy
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Category hierarchy retrieval not implemented - requires repository integration");
    }

    /**
     * Gets all root categories (categories with no parent).
     * @return list of root categories
     */
    public List<DomainEntityCategory> getRootCategories() {
        return categoryService.getRootCategories();
    }

    /**
     * Gets child categories for a given parent category.
     * @param parentId the parent category identifier
     * @param activeOnly whether to return only active categories
     * @return list of child categories
     */
    public List<DomainEntityCategory> getChildCategories(DomainValueCategoryId parentId, boolean activeOnly) {
        return categoryService.getChildCategories(parentId, activeOnly);
    }

    /**
     * Builds a hierarchical category tree from a flat list of categories.
     * @param categories the flat list of categories
     * @return the hierarchical category structure
     */
    public List<DomainEntityCategory> buildCategoryHierarchy(List<DomainEntityCategory> categories) {
        return categoryService.buildCategoryHierarchy(categories);
    }

    /**
     * Validates that a category name is globally unique.
     * @param name the category name to validate
     * @throws IllegalStateException if name already exists
     */
    public void validateCategoryNameUniqueness(String name) {
        categoryService.validateCategoryNameUniqueness(name);
    }

    /**
     * Validates that setting a new parent won't create a circular reference.
     * @param category the category being modified
     * @param newParentId the proposed parent category identifier
     * @throws IllegalArgumentException if a circular reference would be created
     */
    public void validateNoCircularReference(DomainEntityCategory category, DomainValueCategoryId newParentId) {
        categoryService.validateNoCircularReference(category, newParentId);
    }

    /**
     * Validates that a category can be safely deleted.
     * @param categoryId the category identifier to validate
     * @throws IllegalStateException if category cannot be deleted
     */
    public void validateCategoryDeletion(DomainValueCategoryId categoryId) {
        categoryService.validateCategoryDeletion(categoryId);
    }

    /**
     * Deactivates a category.
     * @param categoryId the category identifier
     * @throws IllegalArgumentException if category not found
     */
    public void deactivateCategory(DomainValueCategoryId categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId cannot be null");
        }
        
        // This method would typically get the category from a repository and then deactivate it
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Category deactivation not implemented - requires repository integration");
    }

    /**
     * Activates a category.
     * @param categoryId the category identifier
     * @throws IllegalArgumentException if category not found
     */
    public void activateCategory(DomainValueCategoryId categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId cannot be null");
        }
        
        // This method would typically get the category from a repository and then activate it
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Category activation not implemented - requires repository integration");
    }

    /**
     * Updates the display order of a category.
     * @param categoryId the category identifier
     * @param displayOrder the new display order
     * @throws IllegalArgumentException if category not found or validation fails
     */
    public void updateCategoryDisplayOrder(DomainValueCategoryId categoryId, Integer displayOrder) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId cannot be null");
        }
        if (displayOrder == null || displayOrder < 0) {
            throw new IllegalArgumentException("Display order must be non-negative");
        }
        
        // This method would typically get the category from a repository and then update display order
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Category display order update not implemented - requires repository integration");
    }

    /**
     * Sets the parent category for a category.
     * @param categoryId the category identifier
     * @param parentCategoryId the new parent category identifier (null for root category)
     * @throws IllegalArgumentException if category not found or validation fails
     */
    public void setCategoryParent(DomainValueCategoryId categoryId, DomainValueCategoryId parentCategoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId cannot be null");
        }
        
        // This method would typically get both categories from repository and set parent relationship
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Category parent setting not implemented - requires repository integration");
    }
}