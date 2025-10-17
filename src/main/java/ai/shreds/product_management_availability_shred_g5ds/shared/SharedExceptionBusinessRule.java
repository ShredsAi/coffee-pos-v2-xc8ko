package ai.shreds.product_management_availability_shred_g5ds.shared;

public class SharedExceptionBusinessRule extends RuntimeException {
    private final String ruleName;
    private final String violatedConstraint;

    public SharedExceptionBusinessRule(String message) {
        super(message);
        this.ruleName = null;
        this.violatedConstraint = null;
    }

    public SharedExceptionBusinessRule(String ruleName, String violatedConstraint) {
        super(String.format("Business rule violation: %s - %s", ruleName, violatedConstraint));
        this.ruleName = ruleName;
        this.violatedConstraint = violatedConstraint;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getViolatedConstraint() {
        return violatedConstraint;
    }
}