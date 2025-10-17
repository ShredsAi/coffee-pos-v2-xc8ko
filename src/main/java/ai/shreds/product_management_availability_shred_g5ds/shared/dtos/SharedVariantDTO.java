package ai.shreds.product_management_availability_shred_g5ds.shared.dtos;

import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueNutritionalInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public class SharedVariantDTO {
    @NotNull
    @JsonProperty("variantId")
    private UUID variantId;

    @NotNull
    @JsonProperty("productId")
    private UUID productId;

    @NotBlank
    @Size(min = 1, max = 50)
    @JsonProperty("sizeName")
    private String sizeName;

    @DecimalMin("0.0")
    @JsonProperty("volumeInOz")
    private BigDecimal volumeInOz;

    @DecimalMin("0.0")
    @JsonProperty("volumeInMl")
    private BigDecimal volumeInMl;

    @Size(max = 5)
    @JsonProperty("sizeAbbreviation")
    private String sizeAbbreviation;

    @NotNull
    @Valid
    @JsonProperty("priceModifier")
    private SharedValueMoney priceModifier;

    @Valid
    @JsonProperty("nutritionalInfo")
    private SharedValueNutritionalInfo nutritionalInfo;

    @JsonProperty("isAvailable")
    private Boolean isAvailable = true;

    public SharedVariantDTO() {}

    public SharedVariantDTO(UUID variantId, UUID productId, String sizeName, BigDecimal volumeInOz,
                           BigDecimal volumeInMl, String sizeAbbreviation, SharedValueMoney priceModifier,
                           SharedValueNutritionalInfo nutritionalInfo, Boolean isAvailable) {
        this.variantId = variantId;
        this.productId = productId;
        this.sizeName = sizeName;
        this.volumeInOz = volumeInOz;
        this.volumeInMl = volumeInMl;
        this.sizeAbbreviation = sizeAbbreviation;
        this.priceModifier = priceModifier;
        this.nutritionalInfo = nutritionalInfo;
        this.isAvailable = isAvailable;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public void setVariantId(UUID variantId) {
        this.variantId = variantId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public BigDecimal getVolumeInOz() {
        return volumeInOz;
    }

    public void setVolumeInOz(BigDecimal volumeInOz) {
        this.volumeInOz = volumeInOz;
    }

    public BigDecimal getVolumeInMl() {
        return volumeInMl;
    }

    public void setVolumeInMl(BigDecimal volumeInMl) {
        this.volumeInMl = volumeInMl;
    }

    public String getSizeAbbreviation() {
        return sizeAbbreviation;
    }

    public void setSizeAbbreviation(String sizeAbbreviation) {
        this.sizeAbbreviation = sizeAbbreviation;
    }

    public SharedValueMoney getPriceModifier() {
        return priceModifier;
    }

    public void setPriceModifier(SharedValueMoney priceModifier) {
        this.priceModifier = priceModifier;
    }

    public SharedValueNutritionalInfo getNutritionalInfo() {
        return nutritionalInfo;
    }

    public void setNutritionalInfo(SharedValueNutritionalInfo nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}