package ai.shreds.product_management_availability_shred_g5ds.domain.value_objects;

import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumCurrency;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Domain value object representing a monetary amount with currency.
 * Immutable and provides arithmetic operations with currency validation.
 */
public class DomainValueMoney {
    private final BigDecimal amount;
    private final SharedEnumCurrency currency;

    /**
     * Constructs a Money value object.
     * @param amount the monetary amount
     * @param currency the currency
     * @throws IllegalArgumentException if amount is null or currency is null
     */
    public DomainValueMoney(BigDecimal amount, SharedEnumCurrency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public SharedEnumCurrency getCurrency() {
        return currency;
    }

    /**
     * Adds another Money value to this one.
     * @param other the Money value to add
     * @return a new Money instance with the sum
     * @throws IllegalArgumentException if currencies don't match
     */
    public DomainValueMoney add(DomainValueMoney other) {
        validateSameCurrency(other);
        return new DomainValueMoney(this.amount.add(other.amount), this.currency);
    }

    /**
     * Subtracts another Money value from this one.
     * @param other the Money value to subtract
     * @return a new Money instance with the difference
     * @throws IllegalArgumentException if currencies don't match
     */
    public DomainValueMoney subtract(DomainValueMoney other) {
        validateSameCurrency(other);
        return new DomainValueMoney(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * Checks if this money amount is positive.
     * @return true if amount is greater than zero
     */
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Checks if this money amount is zero.
     * @return true if amount equals zero
     */
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Checks if this money amount is negative.
     * @return true if amount is less than zero
     */
    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Validates that this Money instance has the same currency as another.
     * @param other the other Money instance to compare
     * @throws IllegalArgumentException if currencies don't match
     */
    public void validateSameCurrency(DomainValueMoney other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                String.format("Currency mismatch: %s vs %s", this.currency, other.currency));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        DomainValueMoney that = (DomainValueMoney) other;
        return Objects.equals(amount, that.amount) &&
               Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return String.format("%s %s", currency.getSymbol(), amount);
    }
}