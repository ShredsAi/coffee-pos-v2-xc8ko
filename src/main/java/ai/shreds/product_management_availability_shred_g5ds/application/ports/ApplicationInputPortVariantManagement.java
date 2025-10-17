package ai.shreds.product_management_availability_shred_g5ds.application.ports;

import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOCreateVariant;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOUpdateVariant;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedVariantDTO;

import java.util.List;
import java.util.UUID;

public interface ApplicationInputPortVariantManagement {
    
    /**
     * Creates a new variant for a specific product
     * @param productId The ID of the product to add the variant to
     * @param request The variant creation request containing all necessary information
     * @return The created variant DTO with generated ID
     */
    SharedVariantDTO createVariant(UUID productId, ApplicationDTOCreateVariant request);
    
    /**
     * Updates an existing variant with new information
     * @param productId The ID of the product that owns the variant
     * @param variantId The ID of the variant to update
     * @param request The variant update request containing updated information
     * @return The updated variant DTO
     */
    SharedVariantDTO updateVariant(UUID productId, UUID variantId, ApplicationDTOUpdateVariant request);
    
    /**
     * Retrieves all variants for a specific product
     * @param productId The ID of the product to get variants for
     * @return List of variant DTOs belonging to the product
     */
    List<SharedVariantDTO> getVariantsByProduct(UUID productId);
    
    /**
     * Deletes a variant from a product
     * @param productId The ID of the product that owns the variant
     * @param variantId The ID of the variant to delete
     */
    void deleteVariant(UUID productId, UUID variantId);
}