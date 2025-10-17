package ai.shreds.product_management_availability_shred_g5ds.domain.exceptions;

/**
 * Domain exception thrown when a business rule is violated.
 * Contains information about the rule that was violated and the specific constraint.
 */
public class DomainExceptionBusinessRuleViolation extends RuntimeException {
    private final String ruleName;
    private final String violatedConstraint;

    /**
     * Constructs a new BusinessRuleViolation exception with rule name and constraint.
     * @param ruleName the name of the business rule that was violated
     * @param violatedConstraint the specific constraint that was violated
     */
    public DomainExceptionBusinessRuleViolation(String ruleName, String violatedConstraint) {
        super(String.format("Business rule '%s' violated: %s", ruleName, violatedConstraint));
        this.ruleName = ruleName;
        this.violatedConstraint = violatedConstraint;
    }

    /**
     * Constructs a new BusinessRuleViolation exception with custom message, rule name and constraint.
     * @param message the custom error message
     * @param ruleName the name of the business rule that was violated
     * @param violatedConstraint the specific constraint that was violated
     */
    public DomainExceptionBusinessRuleViolation(String message, String ruleName, String violatedConstraint) {
        super(message);
        this.ruleName = ruleName;
        this.violatedConstraint = violatedConstraint;
    }

    /**
     * Gets the name of the business rule that was violated.
     * @return the rule name
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Gets the specific constraint that was violated.
     * @return the violated constraint
     */
    public String getViolatedConstraint() {
        return violatedConstraint;
    }

    @Override
    public String toString() {
        return String.format("DomainExceptionBusinessRuleViolation{ruleName='%s', violatedConstraint='%s', message='%s'}", 
            ruleName, violatedConstraint, getMessage());
    }
}