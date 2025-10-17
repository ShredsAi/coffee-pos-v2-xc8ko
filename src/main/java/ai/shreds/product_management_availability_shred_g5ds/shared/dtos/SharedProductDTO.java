package ai.shreds.product_management_availability_shred_g5ds.shared.dtos;

import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumProductType;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueNutritionalInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public class SharedProductDTO {
    @NotNull
    @JsonProperty("productId")
    private UUID productId;

    @NotBlank
    @Size(min = 1, max = 100)
    @JsonProperty("name")
    private String name;

    @Size(max = 500)
    @JsonProperty("description")
    private String description;

    @NotNull
    @JsonProperty("productType")
    private SharedEnumProductType productType;

    @NotNull
    @JsonProperty("categoryId")
    private UUID categoryId;

    @NotNull
    @Valid
    @JsonProperty("basePrice")
    private SharedValueMoney basePrice;

    @Valid
    @JsonProperty("nutritionalInfo")
    private SharedValueNutritionalInfo nutritionalInfo;

    @JsonProperty("isActive")
    private Boolean isActive = true;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("lastModified")
    private LocalDateTime lastModified;

    public SharedProductDTO() {}

    public SharedProductDTO(UUID productId, String name, String description, SharedEnumProductType productType,
                           UUID categoryId, SharedValueMoney basePrice, SharedValueNutritionalInfo nutritionalInfo,
                           Boolean isActive, LocalDateTime createdAt, LocalDateTime lastModified) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.productType = productType;
        this.categoryId = categoryId;
        this.basePrice = basePrice;
        this.nutritionalInfo = nutritionalInfo;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
}