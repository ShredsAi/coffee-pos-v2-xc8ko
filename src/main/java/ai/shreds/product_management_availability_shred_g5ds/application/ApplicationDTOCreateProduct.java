package ai.shreds.product_management_availability_shred_g5ds.application;

import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumProductType;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueNutritionalInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public class ApplicationDTOCreateProduct {
    
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Product description cannot exceed 500 characters")
    private String description;
    
    @NotNull(message = "Product type is required")
    private SharedEnumProductType productType;
    
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
    
    @NotNull(message = "Base price is required")
    private SharedValueMoney basePrice;
    
    private SharedValueNutritionalInfo nutritionalInfo;
    
    private List<ApplicationDTOCreateVariant> variants;

    // Default constructor
    public ApplicationDTOCreateProduct() {}

    // Constructor with required fields
    public ApplicationDTOCreateProduct(String name, SharedEnumProductType productType, UUID categoryId, SharedValueMoney basePrice) {
        this.name = name;
        this.productType = productType;
        this.categoryId = categoryId;
        this.basePrice = basePrice;
    }

    // Full constructor
    public ApplicationDTOCreateProduct(String name, String description, SharedEnumProductType productType,
                                     UUID categoryId, SharedValueMoney basePrice, 
                                     SharedValueNutritionalInfo nutritionalInfo,
                                     List<ApplicationDTOCreateVariant> variants) {
        this.name = name;
        this.description = description;
        this.productType = productType;
        this.categoryId = categoryId;
        this.basePrice = basePrice;
        this.nutritionalInfo = nutritionalInfo;
        this.variants = variants;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SharedEnumProductType getProductType() {
        return productType;
    }

    public void setProductType(SharedEnumProductType productType) {
        this.productType = productType;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public SharedValueMoney getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(SharedValueMoney basePrice) {
        this.basePrice = basePrice;
    }

    public SharedValueNutritionalInfo getNutritionalInfo() {
        return nutritionalInfo;
    }

    public void setNutritionalInfo(SharedValueNutritionalInfo nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }

    public List<ApplicationDTOCreateVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ApplicationDTOCreateVariant> variants) {
        this.variants = variants;
    }
}