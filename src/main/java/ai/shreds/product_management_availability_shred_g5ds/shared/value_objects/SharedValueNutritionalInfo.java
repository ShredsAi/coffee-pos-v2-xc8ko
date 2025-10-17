package ai.shreds.product_management_availability_shred_g5ds.shared.value_objects;

import ai.shreds.product_management_availability_shred_g5ds.shared.SharedExceptionValidation;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SharedValueNutritionalInfo {
    @Min(0)
    @JsonProperty("calories")
    private final Integer calories;

    @Min(0)
    @JsonProperty("totalFat")
    private final BigDecimal totalFat;

    @Min(0)
    @JsonProperty("caffeine")
    private final BigDecimal caffeine;

    @JsonProperty("allergens")
    private final List<String> allergens;

    public SharedValueNutritionalInfo(@JsonProperty("calories") Integer calories, 
                                     @JsonProperty("totalFat") BigDecimal totalFat, 
                                     @JsonProperty("caffeine") BigDecimal caffeine, 
                                     @JsonProperty("allergens") List<String> allergens) {
        validateNonNegativeValues(calories, totalFat, caffeine);
        this.calories = calories;
        this.totalFat = totalFat;
        this.caffeine = caffeine;
        this.allergens = allergens != null ? new ArrayList<>(allergens) : new ArrayList<>();
    }

    private void validateNonNegativeValues(Integer calories, BigDecimal totalFat, BigDecimal caffeine) {
        if (calories != null && calories < 0) {
            throw new SharedExceptionValidation("calories", calories, "Calories cannot be negative");
        }
        if (totalFat != null && totalFat.compareTo(BigDecimal.ZERO) < 0) {
            throw new SharedExceptionValidation("totalFat", totalFat, "Total fat cannot be negative");
        }
        if (caffeine != null && caffeine.compareTo(BigDecimal.ZERO) < 0) {
            throw new SharedExceptionValidation("caffeine", caffeine, "Caffeine cannot be negative");
        }
    }

    public boolean hasAllergen(String allergen) {
        if (allergen == null || allergens == null) {
            return false;
        }
        return allergens.contains(allergen);
    }

    public Integer getCalories() {
        return calories;
    }

    public BigDecimal getTotalFat() {
        return totalFat;
    }

    public BigDecimal getCaffeine() {
        return caffeine;
    }

    public List<String> getAllergens() {
        return new ArrayList<>(allergens);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        SharedValueNutritionalInfo that = (SharedValueNutritionalInfo) other;
        return Objects.equals(calories, that.calories) &&
               Objects.equals(totalFat, that.totalFat) &&
               Objects.equals(caffeine, that.caffeine) &&
               Objects.equals(allergens, that.allergens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calories, totalFat, caffeine, allergens);
    }
}