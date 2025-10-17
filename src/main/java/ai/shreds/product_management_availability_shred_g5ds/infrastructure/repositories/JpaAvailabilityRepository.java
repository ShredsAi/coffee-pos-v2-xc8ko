package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAvailabilityRepository extends JpaRepository<InfrastructureEntityAvailability, UUID> {
    
    List<InfrastructureEntityAvailability> findByLocationId(UUID locationId);
    
    Optional<InfrastructureEntityAvailability> findByProductIdAndLocationId(UUID productId, UUID locationId);
    
    List<InfrastructureEntityAvailability> findByLocationIdAndIsAvailable(UUID locationId, Boolean isAvailable);
    
    List<InfrastructureEntityAvailability> findByProductId(UUID productId);
    
    List<InfrastructureEntityAvailability> findByIsAvailable(Boolean isAvailable);
    
    void deleteByProductId(UUID productId);
    
    boolean existsByProductIdAndLocationId(UUID productId, UUID locationId);
}