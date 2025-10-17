package ai.shreds.product_management_availability_shred_g5ds.domain.services;

import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductCreated;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductUpdated;
import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityProduct;
import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityCategory;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortProductRepository;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortCategoryRepository;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortEventPublisher;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumProductType;

import java.util.HashMap;
import java.util.Map;

/**
 * Domain service responsible for product management operations.
 * Handles product lifecycle, business rule validation, and event publishing.
 */
public class DomainServiceProductManagement {
    private final DomainOutputPortProductRepository productRepository;
    private final DomainOutputPortCategoryRepository categoryRepository;
    private final DomainOutputPortEventPublisher eventPublisher;

    /**
     * Constructs the ProductManagement domain service.
     * @param productRepository the product repository port
     * @param categoryRepository the category repository port
     * @param eventPublisher the event publisher port
     */
    public DomainServiceProductManagement(DomainOutputPortProductRepository productRepository,
                                         DomainOutputPortCategoryRepository categoryRepository,
                                         DomainOutputPortEventPublisher eventPublisher) {
        if (productRepository == null) {
            throw new IllegalArgumentException("ProductRepository cannot be null");
        }
        if (categoryRepository == null) {
            throw new IllegalArgumentException("CategoryRepository cannot be null");
        }
        if (eventPublisher == null) {
            throw new IllegalArgumentException("EventPublisher cannot be null");
        }
        
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates a new product with validation and event publishing.
     * @param name the product name
     * @param productType the product type
     * @param categoryId the category identifier
     * @param basePrice the base price
     * @return the created product entity
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if business rules are violated
     */
    public DomainEntityProduct createProduct(String name, SharedEnumProductType productType,
                                            DomainValueCategoryId categoryId, DomainValueMoney basePrice) {
        // Validate category exists
        DomainEntityCategory category = categoryRepository.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with ID " + categoryId + " not found");
        }
        if (!category.getIsActive()) {
            throw new IllegalArgumentException("Cannot create product in inactive category");
        }
        
        // Validate product name uniqueness within category
        validateProductNameUniqueness(name, categoryId);
        
        // Create product entity
        DomainEntityProduct product = new DomainEntityProduct(name, productType, category, basePrice);
        
        // Validate business rules
        validateActiveProductRules(product);
        
        // Save product
        product = productRepository.save(product);
        
        // Publish domain event
        DomainEventProductCreated event = new DomainEventProductCreated(
            product.getId(), product.getName(), categoryId, productType);
        eventPublisher.publishProductCreatedEvent(event);
        
        return product;
    }

    /**
     * Updates an existing product with validation and event publishing.
     * @param product the product to update
     * @param name the new name
     * @param description the new description
     * @return the updated product entity
     * @throws IllegalArgumentException if validation fails
     */
    public DomainEntityProduct updateProduct(DomainEntityProduct product, String name, String description) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        // Capture old values for change tracking
        Map<String, Object> changes = new HashMap<>();
        
        // Track name changes
        if (name != null && !name.equals(product.getName())) {
            // Validate name uniqueness if name is changing
            validateProductNameUniqueness(name, product.getCategory().getId());
            
            Map<String, Object> nameChange = new HashMap<>();
            nameChange.put("oldValue", product.getName());
            nameChange.put("newValue", name);
            changes.put("name", nameChange);
        }
        
        // Track description changes
        if (description != null && !description.equals(product.getDescription())) {
            Map<String, Object> descriptionChange = new HashMap<>();
            descriptionChange.put("oldValue", product.getDescription());
            descriptionChange.put("newValue", description);
            changes.put("description", descriptionChange);
        }
        
        // Update product details
        product.updateDetails(name, description);
        
        // Validate business rules if product is active
        if (product.getIsActive()) {
            validateActiveProductRules(product);
        }
        
        // Save updated product
        product = productRepository.save(product);
        
        // Publish update event if there were changes
        if (!changes.isEmpty()) {
            DomainEventProductUpdated event = new DomainEventProductUpdated(
                product.getId(), product.getName(), changes);
            eventPublisher.publishProductUpdatedEvent(event);
        }
        
        return product;
    }

    /**
     * Deactivates a product and publishes update event.
     * @param product the product to deactivate
     * @return the deactivated product entity
     */
    public DomainEntityProduct deactivateProduct(DomainEntityProduct product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        if (!product.getIsActive()) {
            return product; // Already deactivated
        }
        
        // Capture change for event
        Map<String, Object> changes = new HashMap<>();
        Map<String, Object> activeChange = new HashMap<>();
        activeChange.put("oldValue", true);
        activeChange.put("newValue", false);
        changes.put("isActive", activeChange);
        
        // Deactivate product
        product.deactivate();
        
        // Save updated product
        product = productRepository.save(product);
        
        // Publish update event
        DomainEventProductUpdated event = new DomainEventProductUpdated(
            product.getId(), product.getName(), changes);
        eventPublisher.publishProductUpdatedEvent(event);
        
        return product;
    }

    /**
     * Validates that a product name is unique within its category.
     * @param name the product name to validate
     * @param categoryId the category identifier
     * @throws IllegalStateException if name already exists in category
     */
    public void validateProductNameUniqueness(String name, DomainValueCategoryId categoryId) {
        if (productRepository.existsByNameAndCategory(name, categoryId)) {
            throw new IllegalStateException(
                String.format("Product name '%s' already exists in category %s", name, categoryId));
        }
    }

    /**
     * Validates business rules for active products.
     * @param product the product to validate
     * @throws IllegalStateException if business rules are violated
     */
    public void validateActiveProductRules(DomainEntityProduct product) {
        if (product.getIsActive()) {
            // Rule: Active products must have positive base price
            if (!product.getBasePrice().isPositive()) {
                throw new IllegalStateException("Active products must have a positive base price");
            }
            
            // Rule: Active products must have at least one variant (validated in entity)
            try {
                product.validateBusinessRules();
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Product validation failed: " + e.getMessage(), e);
            }
        }
    }
}