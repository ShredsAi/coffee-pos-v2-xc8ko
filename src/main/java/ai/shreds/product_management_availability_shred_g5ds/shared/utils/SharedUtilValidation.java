package ai.shreds.product_management_availability_shred_g5ds.shared.utils;

import ai.shreds.product_management_availability_shred_g5ds.shared.SharedEnumCurrency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Component
public class SharedUtilValidation {

    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public boolean isValidUUID(String uuid) {
        return uuid != null && UUID_PATTERN.matcher(uuid).matches();
    }

    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isValidProductName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 100;
    }

    public boolean isNonNegativeInteger(Integer value) {
        return value != null && value >= 0;
    }

    public boolean isValidCurrency(String currency) {
        if (currency == null) return false;
        try {
            SharedEnumCurrency.valueOf(currency.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}