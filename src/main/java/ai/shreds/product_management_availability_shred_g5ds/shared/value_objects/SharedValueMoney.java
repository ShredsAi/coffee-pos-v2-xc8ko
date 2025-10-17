package ai.shreds.product_management_availability_shred_g5ds.shared.value_objects;

import ai.shreds.product_management_availability_shred_g5ds.shared.SharedExceptionValidation;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class SharedValueMoney {
    @NotNull
    @DecimalMin("0.0")
    @JsonProperty("amount")
    private final BigDecimal amount;

    @NotNull
    @JsonProperty("currency")
    private final String currency;

    public SharedValueMoney(@JsonProperty("amount") BigDecimal amount, 
                           @JsonProperty("currency") String currency) {
        if (amount == null) {
            throw new SharedExceptionValidation("amount", amount, "Amount cannot be null");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new SharedExceptionValidation("currency", currency, "Currency cannot be null or empty");
        }
        this.amount = amount;
        this.currency = currency.toUpperCase();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public SharedValueMoney add(SharedValueMoney other) {
        validateSameCurrency(other);
        return new SharedValueMoney(this.amount.add(other.amount), this.currency);
    }

    public SharedValueMoney subtract(SharedValueMoney other) {
        validateSameCurrency(other);
        return new SharedValueMoney(this.amount.subtract(other.amount), this.currency);
    }

    private void validateSameCurrency(SharedValueMoney other) {
        if (!this.currency.equals(other.currency)) {
            throw new SharedExceptionValidation("currency", other.currency, 
                "Cannot perform operation with different currencies: " + this.currency + " vs " + other.currency);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        SharedValueMoney that = (SharedValueMoney) other;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return String.format("%s %s", amount, currency);
    }
}