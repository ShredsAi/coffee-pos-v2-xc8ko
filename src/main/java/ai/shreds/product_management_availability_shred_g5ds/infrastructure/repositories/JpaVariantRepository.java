package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaVariantRepository extends JpaRepository<InfrastructureEntityVariant, UUID> {
    
    List<InfrastructureEntityVariant> findByProductId(UUID productId);
    
    List<InfrastructureEntityVariant> findByProductIdAndIsAvailable(UUID productId, Boolean isAvailable);
    
    Integer countByProductId(UUID productId);
    
    List<InfrastructureEntityVariant> findByProductIdOrderBySizeNameAsc(UUID productId);
    
    void deleteByProductId(UUID productId);
    
    boolean existsByProductIdAndIsAvailable(UUID productId, Boolean isAvailable);
}