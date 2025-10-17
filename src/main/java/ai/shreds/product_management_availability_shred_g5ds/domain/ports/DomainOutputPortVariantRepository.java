package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityVariant;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueVariantId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;

import java.util.List;

/**
 * Domain output port for variant repository operations.
 * Defines the contract for variant persistence and retrieval.
 */
public interface DomainOutputPortVariantRepository {
    
    /**
     * Saves a variant entity.
     * @param variant the variant to save
     * @return the saved variant
     */
    DomainEntityVariant save(DomainEntityVariant variant);
    
    /**
     * Finds a variant by its ID.
     * @param variantId the variant identifier
     * @return the variant entity or null if not found
     */
    DomainEntityVariant findById(DomainValueVariantId variantId);
    
    /**
     * Finds all variants for a specific product.
     * @param productId the product identifier
     * @return list of variants belonging to the product
     */
    List<DomainEntityVariant> findByProductId(DomainValueProductId productId);
    
    /**
     * Deletes a variant by its ID.
     * @param variantId the variant identifier
     */
    void delete(DomainValueVariantId variantId);
    
    /**
     * Counts the number of variants for a specific product.
     * @param productId the product identifier
     * @return the count of variants for the product
     */
    Integer countByProductId(DomainValueProductId productId);
}