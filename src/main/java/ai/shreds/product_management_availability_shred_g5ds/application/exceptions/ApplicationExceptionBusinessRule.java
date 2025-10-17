package ai.shreds.product_management_availability_shred_g5ds.application.exceptions;

public class ApplicationExceptionBusinessRule extends RuntimeException {
    
    private final String ruleName;
    private final String violatedConstraint;
    
    public ApplicationExceptionBusinessRule(String ruleName, String violatedConstraint) {
        super(String.format("Business rule '%s' violated: %s", ruleName, violatedConstraint));
        this.ruleName = ruleName;
        this.violatedConstraint = violatedConstraint;
    }
    
    public ApplicationExceptionBusinessRule(String message, String ruleName, String violatedConstraint) {
        super(message);
        this.ruleName = ruleName;
        this.violatedConstraint = violatedConstraint;
    }
    
    public ApplicationExceptionBusinessRule(String message, String ruleName, String violatedConstraint, Throwable cause) {
        super(message, cause);
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