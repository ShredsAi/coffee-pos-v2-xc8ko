package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCategoryRepository extends JpaRepository<InfrastructureEntityCategory, UUID> {
    
    Optional<InfrastructureEntityCategory> findByName(String name);
    
    List<InfrastructureEntityCategory> findByParentCategoryId(UUID parentCategoryId);
    
    List<InfrastructureEntityCategory> findByIsActive(Boolean isActive);
    
    boolean existsByName(String name);
    
    List<InfrastructureEntityCategory> findByIsActiveOrderByDisplayOrderAsc(Boolean isActive);
    
    List<InfrastructureEntityCategory> findByParentCategoryIdIsNull();
    
    List<InfrastructureEntityCategory> findByParentCategoryIdAndIsActive(UUID parentCategoryId, Boolean isActive);
}