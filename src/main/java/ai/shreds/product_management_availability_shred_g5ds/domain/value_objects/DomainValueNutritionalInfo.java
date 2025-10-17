package ai.shreds.product_management_availability_shred_g5ds.domain.value_objects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Domain value object representing nutritional information for products.
 * Immutable and ensures all nutritional values are non-negative.
 */
public class DomainValueNutritionalInfo {
    private final Integer calories;
    private final BigDecimal totalFat;
    private final BigDecimal caffeine;
    private final List<String> allergens;

    /**
     * Constructs a NutritionalInfo value object.
     * @param calories the caloric content
     * @param totalFat the total fat content in grams
     * @param caffeine the caffeine content in milligrams
     * @param allergens the list of allergens
     * @throws IllegalArgumentException if any nutritional value is negative
     */
    public DomainValueNutritionalInfo(Integer calories, BigDecimal totalFat, 
                                     BigDecimal caffeine, List<String> allergens) {
        // Validate parameters before assignment
        if (calories != null && calories < 0) {
            throw new IllegalArgumentException("Calories cannot be negative");
        }
        if (totalFat != null && totalFat.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total fat cannot be negative");
        }
        if (caffeine != null && caffeine.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Caffeine cannot be negative");
        }
        
        this.calories = calories;
        this.totalFat = totalFat;
        this.caffeine = caffeine;
        this.allergens = allergens != null ? new ArrayList<>(allergens) : new ArrayList<>();
    }

    /**
     * Validates that all nutritional values are non-negative.
     * @throws IllegalArgumentException if any value is negative
     */
    public void validateNonNegativeValues() {
        if (calories != null && calories < 0) {
            throw new IllegalArgumentException("Calories cannot be negative");
        }
        if (totalFat != null && totalFat.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total fat cannot be negative");
        }
        if (caffeine != null && caffeine.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Caffeine cannot be negative");
        }
    }

    /**
     * Checks if this nutritional info contains a specific allergen.
     * @param allergen the allergen to check for
     * @return true if the allergen is present, false otherwise
     */
    public boolean hasAllergen(String allergen) {
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
        
        DomainValueNutritionalInfo that = (DomainValueNutritionalInfo) other;
        return Objects.equals(calories, that.calories) &&
               Objects.equals(totalFat, that.totalFat) &&
               Objects.equals(caffeine, that.caffeine) &&
               Objects.equals(allergens, that.allergens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calories, totalFat, caffeine, allergens);
    }

    @Override
    public String toString() {
        return String.format("DomainValueNutritionalInfo{calories=%d, totalFat=%s, caffeine=%s, allergens=%s}",
            calories, totalFat, caffeine, allergens);
    }
}