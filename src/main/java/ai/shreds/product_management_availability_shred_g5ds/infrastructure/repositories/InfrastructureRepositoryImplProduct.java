package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityProduct;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortProductRepository;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;
import ai.shreds.product_management_availability_shred_g5ds.infrastructure.exceptions.InfrastructureExceptionDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class InfrastructureRepositoryImplProduct implements DomainOutputPortProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final InfrastructureMapperProduct productMapper;

    @Autowired
    public InfrastructureRepositoryImplProduct(
            JpaProductRepository jpaProductRepository,
            InfrastructureMapperProduct productMapper) {
        this.jpaProductRepository = jpaProductRepository;
        this.productMapper = productMapper;
    }

    @Override
    public DomainEntityProduct save(DomainEntityProduct product) {
        try {
            InfrastructureEntityProduct entity = productMapper.toEntity(product);
            InfrastructureEntityProduct savedEntity = jpaProductRepository.save(entity);
            return productMapper.toDomain(savedEntity);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to save product", e, "save", "Product"
            );
        }
    }

    @Override
    public DomainEntityProduct findById(DomainValueProductId productId) {
        try {
            Optional<InfrastructureEntityProduct> entity = jpaProductRepository.findById(productId.getValue());
            return entity.map(productMapper::toDomain).orElse(null);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to find product by id", e, "findById", "Product"
            );
        }
    }

    @Override
    public List<DomainEntityProduct> findAll(Integer page, Integer size, DomainValueCategoryId categoryId, Boolean active) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<InfrastructureEntityProduct> entityPage;
            
            if (categoryId != null && active != null) {
                entityPage = jpaProductRepository.findByCategoryIdAndIsActive(
                    categoryId.getValue(), active, pageable
                );
            } else if (active != null) {
                entityPage = jpaProductRepository.findByIsActive(active, pageable);
            } else {
                entityPage = jpaProductRepository.findByFilters(
                    categoryId != null ? categoryId.getValue() : null, 
                    active, 
                    pageable
                );
            }
            
            return productMapper.toDomainList(entityPage.getContent());
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to find products", e, "findAll", "Product"
            );
        }
    }

    @Override
    public boolean existsByNameAndCategory(String name, DomainValueCategoryId categoryId) {
        try {
            return jpaProductRepository.existsByNameAndCategoryId(name, categoryId.getValue());
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to check product existence", e, "existsByNameAndCategory", "Product"
            );
        }
    }

    @Override
    public void delete(DomainValueProductId productId) {
        try {
            jpaProductRepository.deleteById(productId.getValue());
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to delete product", e, "delete", "Product"
            );
        }
    }

    @Override
    public Long count(DomainValueCategoryId categoryId, Boolean active) {
        try {
            if (categoryId != null && active != null) {
                return jpaProductRepository.countByCategoryIdAndIsActive(categoryId.getValue(), active);
            } else {
                return jpaProductRepository.countByFilters(
                    categoryId != null ? categoryId.getValue() : null, 
                    active
                );
            }
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to count products", e, "count", "Product"
            );
        }
    }
}