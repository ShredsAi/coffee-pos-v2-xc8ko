package ai.shreds.product_management_availability_shred_g5ds.application.ports;

import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOCreateProduct;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOUpdateProduct;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedProductDTO;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedPagedResultDTO;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValuePaginationParams;

import java.util.UUID;

public interface ApplicationInputPortProductManagement {
    
    /**
     * Creates a new product with the provided information
     * @param request The product creation request containing all necessary information
     * @return The created product DTO with generated ID and timestamps
     */
    SharedProductDTO createProduct(ApplicationDTOCreateProduct request);
    
    /**
     * Updates an existing product with new information
     * @param productId The ID of the product to update
     * @param request The product update request containing updated information
     * @return The updated product DTO with modified timestamp
     */
    SharedProductDTO updateProduct(UUID productId, ApplicationDTOUpdateProduct request);
    
    /**
     * Retrieves a product by its unique identifier
     * @param productId The unique identifier of the product
     * @return The product DTO if found
     */
    SharedProductDTO getProductById(UUID productId);
    
    /**
     * Lists products with pagination and optional filtering
     * @param params Pagination parameters (page, size, sort)
     * @param categoryId Optional category filter
     * @param active Optional active status filter
     * @return Paginated result containing products and pagination metadata
     */
    SharedPagedResultDTO<SharedProductDTO> listProducts(SharedValuePaginationParams params, UUID categoryId, Boolean active);
    
    /**
     * Deactivates a product, making it unavailable
     * @param productId The ID of the product to deactivate
     */
    void deactivateProduct(UUID productId);
}