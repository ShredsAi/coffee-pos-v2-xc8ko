package ai.shreds.product_management_availability_shred_g5ds.infrastructure.repositories;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityCategory;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortCategoryRepository;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;
import ai.shreds.product_management_availability_shred_g5ds.infrastructure.exceptions.InfrastructureExceptionDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class InfrastructureRepositoryImplCategory implements DomainOutputPortCategoryRepository {

    private final JpaCategoryRepository jpaCategoryRepository;
    private final InfrastructureMapperCategory categoryMapper;

    @Autowired
    public InfrastructureRepositoryImplCategory(
            JpaCategoryRepository jpaCategoryRepository,
            InfrastructureMapperCategory categoryMapper) {
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public DomainEntityCategory save(DomainEntityCategory category) {
        try {
            InfrastructureEntityCategory entity = categoryMapper.toEntity(category);
            InfrastructureEntityCategory savedEntity = jpaCategoryRepository.save(entity);
            return categoryMapper.toDomain(savedEntity);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to save category", e, "save", "Category"
            );
        }
    }

    @Override
    public DomainEntityCategory findById(DomainValueCategoryId categoryId) {
        try {
            Optional<InfrastructureEntityCategory> entity = jpaCategoryRepository.findById(categoryId.getValue());
            return entity.map(categoryMapper::toDomain).orElse(null);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to find category by id", e, "findById", "Category"
            );
        }
    }

    @Override
    public List<DomainEntityCategory> findAll(Boolean active) {
        try {
            List<InfrastructureEntityCategory> entities;
            if (active != null) {
                entities = jpaCategoryRepository.findByIsActiveOrderByDisplayOrderAsc(active);
            } else {
                entities = jpaCategoryRepository.findAll();
            }
            return categoryMapper.toDomainList(entities);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to find categories", e, "findAll", "Category"
            );
        }
    }

    @Override
    public boolean existsByName(String name) {
        try {
            return jpaCategoryRepository.existsByName(name);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to check category existence by name", e, "existsByName", "Category"
            );
        }
    }

    @Override
    public void delete(DomainValueCategoryId categoryId) {
        try {
            jpaCategoryRepository.deleteById(categoryId.getValue());
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to delete category", e, "delete", "Category"
            );
        }
    }

    @Override
    public List<DomainEntityCategory> findByParentId(DomainValueCategoryId parentId) {
        try {
            List<InfrastructureEntityCategory> entities = jpaCategoryRepository.findByParentCategoryId(parentId.getValue());
            return categoryMapper.toDomainList(entities);
        } catch (Exception e) {
            throw new InfrastructureExceptionDatabase(
                "Failed to find categories by parent id", e, "findByParentId", "Category"
            );
        }
    }
}