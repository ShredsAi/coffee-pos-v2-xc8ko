package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityCategory;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;

import java.util.List;

/**
 * Domain output port for category repository operations.
 * Defines the contract for category persistence and retrieval.
 */
public interface DomainOutputPortCategoryRepository {
    
    /**
     * Saves a category entity.
     * @param category the category to save
     * @return the saved category
     */
    DomainEntityCategory save(DomainEntityCategory category);
    
    /**
     * Finds a category by its ID.
     * @param categoryId the category identifier
     * @return the category entity or null if not found
     */
    DomainEntityCategory findById(DomainValueCategoryId categoryId);
    
    /**
     * Finds all categories with optional active filter.
     * @param active the active status filter (optional)
     * @return list of categories matching the criteria
     */
    List<DomainEntityCategory> findAll(Boolean active);
    
    /**
     * Checks if a category with the given name exists.
     * @param name the category name
     * @return true if a category with the name exists
     */
    boolean existsByName(String name);
    
    /**
     * Deletes a category by its ID.
     * @param categoryId the category identifier
     */
    void delete(DomainValueCategoryId categoryId);
    
    /**
     * Finds all child categories of a given parent.
     * @param parentId the parent category identifier
     * @return list of child categories
     */
    List<DomainEntityCategory> findByParentId(DomainValueCategoryId parentId);
}