package ai.shreds.product_management_availability_shred_g5ds.domain.exceptions;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueMoney;

/**
 * Domain exception thrown when an invalid price is encountered.
 * Contains the invalid price and reason for the validation failure.
 */
public class DomainExceptionInvalidPrice extends RuntimeException {
    private final DomainValueMoney invalidPrice;
    private final String reason;

    /**
     * Constructs a new InvalidPrice exception with the invalid price and reason.
     * @param invalidPrice the invalid price that caused the exception
     * @param reason the reason why the price is invalid
     */
    public DomainExceptionInvalidPrice(DomainValueMoney invalidPrice, String reason) {
        super(String.format("Invalid price '%s': %s", invalidPrice, reason));
        this.invalidPrice = invalidPrice;
        this.reason = reason;
    }

    /**
     * Constructs a new InvalidPrice exception with custom message, invalid price and reason.
     * @param message the custom error message
     * @param invalidPrice the invalid price that caused the exception
     * @param reason the reason why the price is invalid
     */
    public DomainExceptionInvalidPrice(String message, DomainValueMoney invalidPrice, String reason) {
        super(message);
        this.invalidPrice = invalidPrice;
        this.reason = reason;
    }

    /**
     * Gets the invalid price that caused the exception.
     * @return the invalid price
     */
    public DomainValueMoney getInvalidPrice() {
        return invalidPrice;
    }

    /**
     * Gets the reason why the price is invalid.
     * @return the reason for invalidity
     */
    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return String.format("DomainExceptionInvalidPrice{invalidPrice=%s, reason='%s', message='%s'}", 
            invalidPrice, reason, getMessage());
    }
}