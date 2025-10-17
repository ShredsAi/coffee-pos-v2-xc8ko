package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityAvailability;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortAvailabilityRepository;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.*;
import ai.shreds.product_management_availability_shred_g5ds.infrastructure.exceptions.InfrastructureExceptionDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class InfrastructureRepositoryImplAvailability implements DomainOutputPortAvailabilityRepository {

    private final JpaAvailabilityRepository jpaAvailabilityRepository;
    private final InfrastructureMapperAvailability availabilityMapper;

    @Autowired
    public InfrastructureRepositoryImplAvailability(
            JpaAvailabilityRepository jpaAvailabilityRepository,
            InfrastructureMapperAvailability availabilityMapper) {
        this.jpaAvailabilityRepository = jpaAvailabilityRepository;
        this.availabilityMapper = availabilityMapper;
    }

    @Override
    public DomainEntityAvailability save(DomainEntityAvailability availability) {
        try {
            InfrastructureEntityAvailability entity = availabilityMapper.toEntity(availability);
            InfrastructureEntityAvailability savedEntity = jpaAvailabilityRepository.save(entity);
            return availabilityMapper.toDomain(savedEntity);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to save availability", e, "save", "Availability"
            );
        }
    }

    @Override
    public DomainEntityAvailability findById(DomainValueAvailabilityId availabilityId) {
        try {
            Optional<InfrastructureEntityAvailability> entity = jpaAvailabilityRepository.findById(availabilityId.getValue());
            return entity.map(availabilityMapper::toDomain).orElse(null);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to find availability by id", e, "findById", "Availability"
            );
        }
    }

    @Override
    public List<DomainEntityAvailability> findByLocationId(DomainValueLocationId locationId) {
        try {
            List<InfrastructureEntityAvailability> entities = jpaAvailabilityRepository.findByLocationId(locationId.getValue());
            return availabilityMapper.toDomainList(entities);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to find availabilities by location id", e, "findByLocationId", "Availability"
            );
        }
    }

    @Override
    public DomainEntityAvailability findByProductAndLocation(DomainValueProductId productId, DomainValueLocationId locationId) {
        try {
            Optional<InfrastructureEntityAvailability> entity = jpaAvailabilityRepository.findByProductIdAndLocationId(
                productId.getValue(), locationId.getValue()
            );
            return entity.map(availabilityMapper::toDomain).orElse(null);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to find availability by product and location", e, "findByProductAndLocation", "Availability"
            );
        }
    }

    @Override
    public void delete(DomainValueAvailabilityId availabilityId) {
        try {
            jpaAvailabilityRepository.deleteById(availabilityId.getValue());
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to delete availability", e, "delete", "Availability"
            );
        }
    }
}