package ai.shreds.product_management_availability_shred_g5ds.domain.services;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityCategory;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortCategoryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Domain service responsible for category management operations.
 * Handles category lifecycle, hierarchical relationships, and business rule validation.
 */
public class DomainServiceCategoryManagement {
    private final DomainOutputPortCategoryRepository categoryRepository;

    /**
     * Constructs the CategoryManagement domain service.
     * @param categoryRepository the category repository port
     */
    public DomainServiceCategoryManagement(DomainOutputPortCategoryRepository categoryRepository) {
        if (categoryRepository == null) {
            throw new IllegalArgumentException("CategoryRepository cannot be null");
        }
        
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new category with validation.
     * @param name the category name
     * @param displayOrder the display order
     * @param parentCategoryId the parent category ID (optional)
     * @return the created category entity
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if business rules are violated
     */
    public DomainEntityCategory createCategory(String name, Integer displayOrder, 
                                              DomainValueCategoryId parentCategoryId) {
        // Validate category name uniqueness
        validateCategoryNameUniqueness(name);
        
        // Create category entity
        DomainEntityCategory category = new DomainEntityCategory(name, displayOrder);
        
        // Set parent category if provided
        if (parentCategoryId != null) {
            DomainEntityCategory parentCategory = categoryRepository.findById(parentCategoryId);
            if (parentCategory == null) {
                throw new IllegalArgumentException("Parent category with ID " + parentCategoryId + " not found");
            }
            if (!parentCategory.getIsActive()) {
                throw new IllegalArgumentException("Cannot create category under inactive parent category");
            }
            
            // Validate no circular reference
            validateNoCircularReference(category, parentCategoryId);
            category.setParentCategory(parentCategory);
        }
        
        // Save category
        return categoryRepository.save(category);
    }

    /**
     * Updates an existing category with validation.
     * @param category the category to update
     * @param name the new name
     * @param description the new description
     * @return the updated category entity
     * @throws IllegalArgumentException if validation fails
     */
    public DomainEntityCategory updateCategory(DomainEntityCategory category, String name, String description) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        // Validate name uniqueness if name is changing
        if (name != null && !name.equals(category.getName())) {
            validateCategoryNameUniqueness(name);
            category.updateName(name);
        }
        
        // Update description
        if (description != null) {
            category.updateDescription(description);
        }
        
        // Save updated category
        return categoryRepository.save(category);
    }

    /**
     * Validates that a category name is globally unique.
     * @param name the category name to validate
     * @throws IllegalStateException if name already exists
     */
    public void validateCategoryNameUniqueness(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalStateException(
                String.format("Category name '%s' already exists", name));
        }
    }

    /**
     * Validates that setting a new parent won't create a circular reference.
     * @param category the category being modified
     * @param newParentId the proposed parent category ID
     * @throws IllegalArgumentException if a circular reference would be created
     */
    public void validateNoCircularReference(DomainEntityCategory category, DomainValueCategoryId newParentId) {
        if (newParentId == null) {
            return; // No parent, no circular reference possible
        }
        
        // Check if the new parent is the category itself
        if (newParentId.equals(category.getId())) {
            throw new IllegalArgumentException("Category cannot be its own parent");
        }
        
        // Get the proposed parent category
        DomainEntityCategory newParent = categoryRepository.findById(newParentId);
        if (newParent == null) {
            throw new IllegalArgumentException("Parent category not found: " + newParentId);
        }
        
        // Check if this category is already an ancestor of the new parent
        DomainEntityCategory current = newParent.getParentCategory();
        while (current != null) {
            if (current.getId().equals(category.getId())) {
                throw new IllegalArgumentException(
                    String.format("Circular reference detected: Category %s is already an ancestor of %s", 
                            category.getId(), newParentId));
            }
            current = current.getParentCategory();
        }
    }

    /**
     * Builds a hierarchical category tree from a flat list of categories.
     * @param categories the flat list of categories
     * @return the hierarchical category structure
     */
    public List<DomainEntityCategory> buildCategoryHierarchy(List<DomainEntityCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Create maps for efficient lookup
        Map<DomainValueCategoryId, DomainEntityCategory> categoryMap = new HashMap<>();
        Map<DomainValueCategoryId, List<DomainEntityCategory>> childrenMap = new HashMap<>();
        
        // Populate category map and initialize children lists
        for (DomainEntityCategory category : categories) {
            categoryMap.put(category.getId(), category);
            childrenMap.put(category.getId(), new ArrayList<>());
        }
        
        // Build parent-child relationships
        List<DomainEntityCategory> rootCategories = new ArrayList<>();
        for (DomainEntityCategory category : categories) {
            if (category.isRootCategory()) {
                rootCategories.add(category);
            } else {
                DomainEntityCategory parent = category.getParentCategory();
                if (parent != null && childrenMap.containsKey(parent.getId())) {
                    childrenMap.get(parent.getId()).add(category);
                }
            }
        }
        
        // Sort root categories by display order
        rootCategories.sort((c1, c2) -> {
            int order1 = c1.getDisplayOrder() != null ? c1.getDisplayOrder() : 0;
            int order2 = c2.getDisplayOrder() != null ? c2.getDisplayOrder() : 0;
            return Integer.compare(order1, order2);
        });
        
        // Sort children by display order for each category
        for (List<DomainEntityCategory> children : childrenMap.values()) {
            children.sort((c1, c2) -> {
                int order1 = c1.getDisplayOrder() != null ? c1.getDisplayOrder() : 0;
                int order2 = c2.getDisplayOrder() != null ? c2.getDisplayOrder() : 0;
                return Integer.compare(order1, order2);
            });
        }
        
        return rootCategories;
    }

    /**
     * Gets all active root categories (categories with no parent).
     * @return list of root categories
     */
    public List<DomainEntityCategory> getRootCategories() {
        return categoryRepository.findAll(true)
                .stream()
                .filter(DomainEntityCategory::isRootCategory)
                .sorted((c1, c2) -> {
                    int order1 = c1.getDisplayOrder() != null ? c1.getDisplayOrder() : 0;
                    int order2 = c2.getDisplayOrder() != null ? c2.getDisplayOrder() : 0;
                    return Integer.compare(order1, order2);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets all child categories for a given parent category.
     * @param parentId the parent category ID
     * @param activeOnly whether to return only active categories
     * @return list of child categories
     */
    public List<DomainEntityCategory> getChildCategories(DomainValueCategoryId parentId, boolean activeOnly) {
        List<DomainEntityCategory> children = categoryRepository.findByParentId(parentId);
        
        if (activeOnly) {
            children = children.stream()
                    .filter(DomainEntityCategory::getIsActive)
                    .collect(Collectors.toList());
        }
        
        // Sort by display order
        children.sort((c1, c2) -> {
            int order1 = c1.getDisplayOrder() != null ? c1.getDisplayOrder() : 0;
            int order2 = c2.getDisplayOrder() != null ? c2.getDisplayOrder() : 0;
            return Integer.compare(order1, order2);
        });
        
        return children;
    }

    /**
     * Validates that a category can be safely deleted.
     * @param categoryId the category ID to validate
     * @throws IllegalStateException if category cannot be deleted
     */
    public void validateCategoryDeletion(DomainValueCategoryId categoryId) {
        // Check if category has child categories
        List<DomainEntityCategory> children = categoryRepository.findByParentId(categoryId);
        if (!children.isEmpty()) {
            throw new IllegalStateException(
                String.format("Cannot delete category %s: it has %d child categories", 
                        categoryId, children.size()));
        }
        
        // Additional validation could include checking for products in this category
        // This would require a product repository dependency or a separate validation service
    }
}