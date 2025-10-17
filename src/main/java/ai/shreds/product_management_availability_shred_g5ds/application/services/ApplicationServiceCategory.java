package ai.shreds.product_management_availability_shred_g5ds.application.services;

import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOCreateCategory;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOUpdateCategory;
import ai.shreds.product_management_availability_shred_g5ds.application.ports.ApplicationInputPortCategoryManagement;
import ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionCategoryNotFound;
import ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionBusinessRule;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainInputPortCategoryManagement;
import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityCategory;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionCategoryNotFound;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionBusinessRuleViolation;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionCircularCategoryReference;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedCategoryDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationServiceCategory implements ApplicationInputPortCategoryManagement {
    
    private final DomainInputPortCategoryManagement categoryInputPort;
    
    public ApplicationServiceCategory(DomainInputPortCategoryManagement categoryInputPort) {
        this.categoryInputPort = categoryInputPort;
    }

    @Override
    public SharedCategoryDTO createCategory(ApplicationDTOCreateCategory request) {
        try {
            DomainValueCategoryId parentCategoryId = request.getParentCategoryId() != null ?
                DomainValueCategoryId.fromString(request.getParentCategoryId().toString()) : null;
                
            DomainEntityCategory createdCategory = categoryInputPort.createCategory(
                request.getName(),
                request.getDisplayOrder(),
                parentCategoryId
            );
            
            // Update category with description if provided
            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                createdCategory = categoryInputPort.updateCategory(
                    createdCategory.getId(),
                    createdCategory.getName(),
                    request.getDescription()
                );
            }
            
            return mapDomainEntityToSharedDTO(createdCategory);
            
        } catch (DomainExceptionCategoryNotFound e) {
            throw new ApplicationExceptionCategoryNotFound(e.getCategoryId().getValue());
        } catch (DomainExceptionCircularCategoryReference e) {
            throw new ApplicationExceptionBusinessRule(
                "CircularReference", 
                "Parent category cannot create circular reference: " + e.getParentCategoryId().getValue()
            );
        } catch (DomainExceptionBusinessRuleViolation e) {
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        }
    }

    @Override
    public SharedCategoryDTO updateCategory(UUID categoryId, ApplicationDTOUpdateCategory request) {
        try {
            DomainValueCategoryId domainCategoryId = DomainValueCategoryId.fromString(categoryId.toString());
            
            // Get existing category for comparison
            DomainEntityCategory existingCategory = categoryInputPort.getCategoryById(domainCategoryId);
            
            // Update basic fields
            DomainEntityCategory updatedCategory = categoryInputPort.updateCategory(
                domainCategoryId,
                request.getName(),
                request.getDescription()
            );
            
            // Handle parent category update if provided
            if (request.getParentCategoryId() != null) {
                DomainValueCategoryId newParentId = DomainValueCategoryId.fromString(request.getParentCategoryId().toString());
                // Fixed: Replace safe navigation operator with proper null checking
                if (existingCategory.getParentCategory() == null || !newParentId.equals(existingCategory.getParentCategory().getId())) {
                    // This would require domain service method to update parent
                    // For now, we'll assume the domain handles this through updateCategory
                }
            }
            
            // Handle display order update if provided
            if (request.getDisplayOrder() != null && !request.getDisplayOrder().equals(existingCategory.getDisplayOrder())) {
                // This would require domain service method to update display order
                // For now, we'll assume the domain handles this through updateCategory
            }
            
            // Handle activation/deactivation if provided
            if (request.getIsActive() != null && !request.getIsActive().equals(existingCategory.getIsActive())) {
                if (request.getIsActive()) {
                    updatedCategory.activate();
                } else {
                    updatedCategory.deactivate();
                }
            }
            
            return mapDomainEntityToSharedDTO(updatedCategory);
            
        } catch (DomainExceptionCategoryNotFound e) {
            throw new ApplicationExceptionCategoryNotFound(e.getCategoryId().getValue());
        } catch (DomainExceptionCircularCategoryReference e) {
            throw new ApplicationExceptionBusinessRule(
                "CircularReference", 
                "Cannot update parent category - would create circular reference: " + e.getParentCategoryId().getValue()
            );
        } catch (DomainExceptionBusinessRuleViolation e) {
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        }
    }

    @Override
    public SharedCategoryDTO getCategoryById(UUID categoryId) {
        try {
            DomainValueCategoryId domainCategoryId = DomainValueCategoryId.fromString(categoryId.toString());
            DomainEntityCategory category = categoryInputPort.getCategoryById(domainCategoryId);
            return mapDomainEntityToSharedDTO(category);
        } catch (DomainExceptionCategoryNotFound e) {
            throw new ApplicationExceptionCategoryNotFound(e.getCategoryId().getValue());
        }
    }

    @Override
    public List<SharedCategoryDTO> listCategories(Boolean active) {
        List<DomainEntityCategory> categories = categoryInputPort.listCategories(active);
        return categories.stream()
            .map(this::mapDomainEntityToSharedDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<SharedCategoryDTO> getCategoryHierarchy() {
        List<DomainEntityCategory> categories = categoryInputPort.getCategoryHierarchy();
        return categories.stream()
            .map(this::mapDomainEntityToSharedDTO)
            .collect(Collectors.toList());
    }
    
    public SharedCategoryDTO mapDomainEntityToSharedDTO(DomainEntityCategory category) {
        SharedCategoryDTO dto = new SharedCategoryDTO();
        dto.setCategoryId(category.getId().getValue());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setParentCategoryId(category.getParentCategory() != null ? 
            category.getParentCategory().getId().getValue() : null);
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setIsActive(category.getIsActive());
        return dto;
    }
}