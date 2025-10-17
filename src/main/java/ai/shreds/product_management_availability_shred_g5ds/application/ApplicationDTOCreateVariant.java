package ai.shreds.product_management_availability_shred_g5ds.application;

import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueMoney;
import ai.shreds.product_management_availability_shred_g5ds.shared.value_objects.SharedValueNutritionalInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class ApplicationDTOCreateVariant {
    
    @NotBlank(message = "Size name is required")
    @Size(max = 50, message = "Size name cannot exceed 50 characters")
    private String sizeName;
    
    @Positive(message = "Volume in ounces must be positive")
    private BigDecimal volumeInOz;
    
    @Positive(message = "Volume in milliliters must be positive")
    private BigDecimal volumeInMl;
    
    @Size(max = 5, message = "Size abbreviation cannot exceed 5 characters")
    private String sizeAbbreviation;
    
    @NotNull(message = "Price modifier is required")
    private SharedValueMoney priceModifier;
    
    private SharedValueNutritionalInfo nutritionalInfo;

    // Default constructor
    public ApplicationDTOCreateVariant() {}

    // Constructor with required fields
    public ApplicationDTOCreateVariant(String sizeName, SharedValueMoney priceModifier) {
        this.sizeName = sizeName;
        this.priceModifier = priceModifier;
    }

    // Full constructor
    public ApplicationDTOCreateVariant(String sizeName, BigDecimal volumeInOz, BigDecimal volumeInMl,
                                     String sizeAbbreviation, SharedValueMoney priceModifier,
                                     SharedValueNutritionalInfo nutritionalInfo) {
        this.sizeName = sizeName;
        this.volumeInOz = volumeInOz;
        this.volumeInMl = volumeInMl;
        this.sizeAbbreviation = sizeAbbreviation;
        this.priceModifier = priceModifier;
        this.nutritionalInfo = nutritionalInfo;
    }

    // Getters and Setters
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
}