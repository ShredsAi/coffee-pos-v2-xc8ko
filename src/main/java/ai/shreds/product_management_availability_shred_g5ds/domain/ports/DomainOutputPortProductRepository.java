package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityProduct;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;

import java.util.List;

/**
 * Domain output port for product repository operations.
 * Defines the contract for product persistence and retrieval.
 */
public interface DomainOutputPortProductRepository {
    
    /**
     * Saves a product entity.
     * @param product the product to save
     * @return the saved product
     */
    DomainEntityProduct save(DomainEntityProduct product);
    
    /**
     * Finds a product by its ID.
     * @param productId the product identifier
     * @return the product entity or null if not found
     */
    DomainEntityProduct findById(DomainValueProductId productId);
    
    /**
     * Finds products with pagination and filtering.
     * @param page the page number (zero-based)
     * @param size the page size
     * @param categoryId the category filter (optional)
     * @param active the active status filter (optional)
     * @return list of products matching the criteria
     */
    List<DomainEntityProduct> findAll(Integer page, Integer size, DomainValueCategoryId categoryId, Boolean active);
    
    /**
     * Checks if a product with the given name exists in the specified category.
     * @param name the product name
     * @param categoryId the category identifier
     * @return true if a product with the name exists in the category
     */
    boolean existsByNameAndCategory(String name, DomainValueCategoryId categoryId);
    
    /**
     * Deletes a product by its ID.
     * @param productId the product identifier
     */
    void delete(DomainValueProductId productId);
    
    /**
     * Counts products matching the given criteria.
     * @param categoryId the category filter (optional)
     * @param active the active status filter (optional)
     * @return the count of matching products
     */
    Long count(DomainValueCategoryId categoryId, Boolean active);
}