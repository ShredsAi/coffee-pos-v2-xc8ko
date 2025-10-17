package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaProductRepository extends JpaRepository<InfrastructureEntityProduct, UUID> {
    
    Optional<InfrastructureEntityProduct> findByNameAndCategoryId(String name, UUID categoryId);
    
    Page<InfrastructureEntityProduct> findByCategoryIdAndIsActive(UUID categoryId, Boolean isActive, Pageable pageable);
    
    Page<InfrastructureEntityProduct> findByIsActive(Boolean isActive, Pageable pageable);
    
    Long countByCategoryIdAndIsActive(UUID categoryId, Boolean isActive);
    
    boolean existsByNameAndCategoryId(String name, UUID categoryId);
    
    @Query("SELECT p FROM InfrastructureEntityProduct p WHERE " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:isActive IS NULL OR p.isActive = :isActive)")
    Page<InfrastructureEntityProduct> findByFilters(@Param("categoryId") UUID categoryId, 
                                                    @Param("isActive") Boolean isActive, 
                                                    Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM InfrastructureEntityProduct p WHERE " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:isActive IS NULL OR p.isActive = :isActive)")
    Long countByFilters(@Param("categoryId") UUID categoryId, @Param("isActive") Boolean isActive);
}