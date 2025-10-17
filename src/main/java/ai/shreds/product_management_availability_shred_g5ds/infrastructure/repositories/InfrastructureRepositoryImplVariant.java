package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityVariant;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortVariantRepository;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueVariantId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.infrastructure.exceptions.InfrastructureExceptionDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class InfrastructureRepositoryImplVariant implements DomainOutputPortVariantRepository {

    private final JpaVariantRepository jpaVariantRepository;
    private final InfrastructureMapperVariant variantMapper;

    @Autowired
    public InfrastructureRepositoryImplVariant(
            JpaVariantRepository jpaVariantRepository,
            InfrastructureMapperVariant variantMapper) {
        this.jpaVariantRepository = jpaVariantRepository;
        this.variantMapper = variantMapper;
    }

    @Override
    public DomainEntityVariant save(DomainEntityVariant variant) {
        try {
            InfrastructureEntityVariant entity = variantMapper.toEntity(variant);
            InfrastructureEntityVariant savedEntity = jpaVariantRepository.save(entity);
            return variantMapper.toDomain(savedEntity);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to save variant", e, "save", "Variant"
            );
        }
    }

    @Override
    public DomainEntityVariant findById(DomainValueVariantId variantId) {
        try {
            Optional<InfrastructureEntityVariant> entity = jpaVariantRepository.findById(variantId.getValue());
            return entity.map(variantMapper::toDomain).orElse(null);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to find variant by id", e, "findById", "Variant"
            );
        }
    }

    @Override
    public List<DomainEntityVariant> findByProductId(DomainValueProductId productId) {
        try {
            List<InfrastructureEntityVariant> entities = jpaVariantRepository.findByProductIdOrderBySizeNameAsc(productId.getValue());
            return variantMapper.toDomainList(entities);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to find variants by product id", e, "findByProductId", "Variant"
            );
        }
    }

    @Override
    public void delete(DomainValueVariantId variantId) {
        try {
            jpaVariantRepository.deleteById(variantId.getValue());
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to delete variant", e, "delete", "Variant"
            );
        }
    }

    @Override
    public Integer countByProductId(DomainValueProductId productId) {
        try {
            return jpaVariantRepository.countByProductId(productId.getValue());
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to count variants by product id", e, "countByProductId", "Variant"
            );
        }
    }
}