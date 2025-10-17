package ai.shreds.product_management_availability_shred_g5ds.application.ports;

import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOCreateCategory;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOUpdateCategory;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedCategoryDTO;

import java.util.List;
import java.util.UUID;

public interface ApplicationInputPortCategoryManagement {
    
    /**
     * Creates a new category with the provided information
     * @param request The category creation request containing all necessary information
     * @return The created category DTO with generated ID
     */
    SharedCategoryDTO createCategory(ApplicationDTOCreateCategory request);
    
    /**
     * Updates an existing category with new information
     * @param categoryId The ID of the category to update
     * @param request The category update request containing updated information
     * @return The updated category DTO
     */
    SharedCategoryDTO updateCategory(UUID categoryId, ApplicationDTOUpdateCategory request);
    
    /**
     * Retrieves a category by its unique identifier
     * @param categoryId The unique identifier of the category
     * @return The category DTO if found
     */
    SharedCategoryDTO getCategoryById(UUID categoryId);
    
    /**
     * Lists all categories with optional active status filtering
     * @param active Optional active status filter
     * @return List of category DTOs
     */
    List<SharedCategoryDTO> listCategories(Boolean active);
    
    /**
     * Retrieves the complete category hierarchy
     * @return List of category DTOs organized in hierarchical structure
     */
    List<SharedCategoryDTO> getCategoryHierarchy();
}