package ai.shreds.product_management_availability_shred_g5ds.application.services;

import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOCreateProduct;
import ai.shreds.product_management_availability_shred_g5ds.application.ApplicationDTOUpdateProduct;
import ai.shreds.product_management_availability_shred_g5ds.application.ports.ApplicationInputPortProductManagement;
import ai.shreds.product_management_availability_shred_g5ds.application.ports.ApplicationOutputPortEventPublisher;
import ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionProductNotFound;
import ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionBusinessRule;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainInputPortProductManagement;
import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityProduct;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueNutritionalInfo;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionProductNotFound;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionBusinessRuleViolation;
import ai.shreds.product_management_availability_shred_g5ds.domain.exceptions.DomainExceptionCategoryNotFound;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductCreated;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductUpdated;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedProductDTO;
import ai.shreds.product_management_availability_shred_g5ds.shared.dtos.SharedPagedResultDTO;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValuePaginationParams;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueNutritionalInfo;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumCurrency;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationServiceProduct implements ApplicationInputPortProductManagement {
    
    private final DomainInputPortProductManagement productInputPort;
    private final ApplicationOutputPortEventPublisher eventPublisher;
    
    public ApplicationServiceProduct(DomainInputPortProductManagement productInputPort,
                                   ApplicationOutputPortEventPublisher eventPublisher) {
        this.productInputPort = productInputPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public SharedProductDTO createProduct(ApplicationDTOCreateProduct request) {
        try {
            // Convert application DTO to domain value objects
            DomainValueCategoryId categoryId = DomainValueCategoryId.fromString(request.getCategoryId().toString());
            DomainValueMoney basePrice = new DomainValueMoney(
                request.getBasePrice().getAmount(), 
                SharedEnumCurrency.valueOf(request.getBasePrice().getCurrency())
            );
            
            // Create product through domain layer
            DomainEntityProduct createdProduct = productInputPort.createProduct(
                request.getName(),
                request.getProductType(),
                categoryId,
                basePrice
            );
            
            // Update additional fields if provided
            if (request.getDescription() != null) {
                createdProduct = productInputPort.updateProduct(
                    createdProduct.getId(),
                    createdProduct.getName(),
                    request.getDescription()
                );
            }
            
            // Publish product created event
            DomainEventProductCreated event = new DomainEventProductCreated(
                createdProduct.getId(),
                createdProduct.getName(),
                createdProduct.getCategory().getId(),
                createdProduct.getProductType()
            );
            eventPublisher.publishProductCreatedEvent(event);
            
            // Convert domain entity back to shared DTO
            return mapDomainEntityToSharedDTO(createdProduct);
            
        } catch (DomainExceptionProductNotFound e) {
            throw new ApplicationExceptionProductNotFound(e.getProductId().getValue());
        } catch (DomainExceptionCategoryNotFound e) {
            throw new ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionCategoryNotFound(e.getCategoryId().getValue());
        } catch (DomainExceptionBusinessRuleViolation e) {
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        }
    }

    @Override
    public SharedProductDTO updateProduct(UUID productId, ApplicationDTOUpdateProduct request) {
        try {
            DomainValueProductId domainProductId = DomainValueProductId.fromString(productId.toString());
            
            // Get original product for event comparison
            DomainEntityProduct originalProduct = productInputPort.getProductById(domainProductId);
            
            // Update product through domain layer
            DomainEntityProduct updatedProduct = productInputPort.updateProduct(
                domainProductId,
                request.getName(),
                request.getDescription()
            );
            
            // Create changes map for event
            java.util.Map<String, Object> changes = new java.util.HashMap<>();
            if (!originalProduct.getName().equals(updatedProduct.getName())) {
                changes.put("name", java.util.Map.of("old", originalProduct.getName(), "new", updatedProduct.getName()));
            }
            if (!java.util.Objects.equals(originalProduct.getDescription(), updatedProduct.getDescription())) {
                changes.put("description", java.util.Map.of("old", originalProduct.getDescription(), "new", updatedProduct.getDescription()));
            }
            
            // Publish product updated event if there were changes
            if (!changes.isEmpty()) {
                DomainEventProductUpdated event = new DomainEventProductUpdated(
                    updatedProduct.getId(),
                    updatedProduct.getName(),
                    changes
                );
                eventPublisher.publishProductUpdatedEvent(event);
            }
            
            return mapDomainEntityToSharedDTO(updatedProduct);
            
        } catch (DomainExceptionProductNotFound e) {
            throw new ApplicationExceptionProductNotFound(e.getProductId().getValue());
        } catch (DomainExceptionBusinessRuleViolation e) {
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        }
    }

    @Override
    public SharedProductDTO getProductById(UUID productId) {
        try {
            DomainValueProductId domainProductId = DomainValueProductId.fromString(productId.toString());
            DomainEntityProduct product = productInputPort.getProductById(domainProductId);
            return mapDomainEntityToSharedDTO(product);
        } catch (DomainExceptionProductNotFound e) {
            throw new ApplicationExceptionProductNotFound(e.getProductId().getValue());
        }
    }

    @Override
    public SharedPagedResultDTO<SharedProductDTO> listProducts(SharedValuePaginationParams params, UUID categoryId, Boolean active) {
        try {
            DomainValueCategoryId domainCategoryId = categoryId != null ? 
                DomainValueCategoryId.fromString(categoryId.toString()) : null;
                
            List<DomainEntityProduct> products = productInputPort.listProducts(
                params.getPage(),
                params.getSize(),
                domainCategoryId,
                active
            );
            
            List<SharedProductDTO> productDTOs = products.stream()
                .map(this::mapDomainEntityToSharedDTO)
                .collect(Collectors.toList());
            
            // Get total count from domain layer - this should be implemented in repository
            // For now, calculate based on current page results
            long totalElements = productDTOs.size() == params.getSize() ? 
                (long) params.getSize() * (params.getPage() + 2) : 
                (long) params.getSize() * params.getPage() + productDTOs.size();
            
            int totalPages = (int) Math.ceil((double) totalElements / params.getSize());
            
            SharedPagedResultDTO<SharedProductDTO> result = new SharedPagedResultDTO<>();
            result.setContent(productDTOs);
            result.setTotalElements(totalElements);
            result.setTotalPages(totalPages);
            result.setCurrentPage(params.getPage());
            result.setPageSize(params.getSize());
            result.setHasNext(params.getPage() < totalPages - 1);
            result.setHasPrevious(params.getPage() > 0);
            
            return result;
            
        } catch (DomainExceptionCategoryNotFound e) {
            throw new ai.shreds.product_management_availability_shred_g5ds.application.exceptions.ApplicationExceptionCategoryNotFound(e.getCategoryId().getValue());
        }
    }

    @Override
    public void deactivateProduct(UUID productId) {
        try {
            DomainValueProductId domainProductId = DomainValueProductId.fromString(productId.toString());
            
            // Get product before deactivation for event
            DomainEntityProduct product = productInputPort.getProductById(domainProductId);
            
            productInputPort.deactivateProduct(domainProductId);
            
            // Publish product updated event for deactivation
            java.util.Map<String, Object> changes = new java.util.HashMap<>();
            changes.put("isActive", java.util.Map.of("old", true, "new", false));
            
            DomainEventProductUpdated event = new DomainEventProductUpdated(
                product.getId(),
                product.getName(),
                changes
            );
            eventPublisher.publishProductUpdatedEvent(event);
            
        } catch (DomainExceptionProductNotFound e) {
            throw new ApplicationExceptionProductNotFound(e.getProductId().getValue());
        } catch (DomainExceptionBusinessRuleViolation e) {
            throw new ApplicationExceptionBusinessRule(e.getRuleName(), e.getViolatedConstraint());
        }
    }
    
    public SharedProductDTO mapDomainEntityToSharedDTO(DomainEntityProduct product) {
        SharedValueMoney sharedBasePrice = null;
        if (product.getBasePrice() != null) {
            sharedBasePrice = new SharedValueMoney(
                product.getBasePrice().getAmount(),
                product.getBasePrice().getCurrency().getSymbol()
            );
        }
        
        SharedValueNutritionalInfo sharedNutritionalInfo = null;
        if (product.getNutritionalInfo() != null) {
            sharedNutritionalInfo = new SharedValueNutritionalInfo(
                product.getNutritionalInfo().getCalories(),
                product.getNutritionalInfo().getTotalFat(),
                product.getNutritionalInfo().getCaffeine(),
                product.getNutritionalInfo().getAllergens()
            );
        }
        
        return new SharedProductDTO(
            product.getId().getValue(),
            product.getName(),
            product.getDescription(),
            product.getProductType(),
            product.getCategory().getId().getValue(),
            sharedBasePrice,
            sharedNutritionalInfo,
            product.getIsActive(),
            product.getCreatedAt(),
            product.getLastModified()
        );
    }
}