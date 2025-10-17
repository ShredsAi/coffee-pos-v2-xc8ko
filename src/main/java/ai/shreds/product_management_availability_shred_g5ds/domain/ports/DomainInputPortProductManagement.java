package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.entities.DomainEntityProduct;
import ai.shreds.product_management_availability_shred_g5ds.domain.services.DomainServiceProductManagement;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumProductType;

import java.util.List;

/**
 * Domain input port for product management operations.
 * Acts as a facade for product-related domain services and provides
 * a clean interface for the application layer.
 */
public class DomainInputPortProductManagement {
    private final DomainServiceProductManagement productService;

    /**
     * Constructs the ProductManagement domain input port.
     * @param productService the product domain service
     */
    public DomainInputPortProductManagement(DomainServiceProductManagement productService) {
        if (productService == null) {
            throw new IllegalArgumentException("ProductService cannot be null");
        }
        
        this.productService = productService;
    }

    /**
     * Creates a new product with validation and business rule enforcement.
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
        return productService.createProduct(name, productType, categoryId, basePrice);
    }

    /**
     * Updates an existing product with validation.
     * @param productId the product identifier
     * @param name the new name
     * @param description the new description
     * @return the updated product entity
     * @throws IllegalArgumentException if product not found or validation fails
     */
    public DomainEntityProduct updateProduct(DomainValueProductId productId, String name, String description) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        
        // This method would typically get the product from a repository
        // For now, we'll assume the product is retrieved through the service layer
        // In a complete implementation, this would be handled by a repository port
        throw new UnsupportedOperationException("Product retrieval for update not implemented - requires repository integration");
    }

    /**
     * Gets a product by its identifier.
     * @param productId the product identifier
     * @return the product entity
     * @throws IllegalArgumentException if product not found
     */
    public DomainEntityProduct getProductById(DomainValueProductId productId) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        
        // This method would typically get the product from a repository
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Product retrieval not implemented - requires repository integration");
    }

    /**
     * Lists products with pagination and filtering.
     * @param page the page number (0-based)
     * @param size the page size
     * @param categoryId the category filter (optional)
     * @param active the active status filter (optional)
     * @return list of products
     */
    public List<DomainEntityProduct> listProducts(Integer page, Integer size, 
                                                  DomainValueCategoryId categoryId, Boolean active) {
        if (page != null && page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size != null && size <= 0) {
            throw new IllegalArgumentException("Page size must be positive");
        }
        
        // This method would typically list products from a repository
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Product listing not implemented - requires repository integration");
    }

    /**
     * Deactivates a product.
     * @param productId the product identifier
     * @throws IllegalArgumentException if product not found
     */
    public void deactivateProduct(DomainValueProductId productId) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        
        // This method would typically get the product from a repository and then deactivate it
        // For now, we'll indicate this requires repository integration
        throw new UnsupportedOperationException("Product deactivation not implemented - requires repository integration");
    }

    /**
     * Validates that a product name is unique within its category.
     * @param name the product name to validate
     * @param categoryId the category identifier
     * @throws IllegalStateException if name already exists in category
     */
    public void validateProductNameUniqueness(String name, DomainValueCategoryId categoryId) {
        productService.validateProductNameUniqueness(name, categoryId);
    }

    /**
     * Validates business rules for active products.
     * @param product the product to validate
     * @throws IllegalStateException if business rules are violated
     */
    public void validateActiveProductRules(DomainEntityProduct product) {
        productService.validateActiveProductRules(product);
    }
}