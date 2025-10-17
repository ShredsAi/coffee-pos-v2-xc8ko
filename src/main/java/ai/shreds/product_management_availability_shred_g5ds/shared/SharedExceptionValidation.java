package ai.shreds.product_management_availability_shred_g5ds.shared;

import java.util.ArrayList;
import java.util.List;

public class SharedExceptionValidation extends RuntimeException {
    private final String field;
    private final Object value;
    private final List<String> validationErrors;

    public SharedExceptionValidation(String message) {
        super(message);
        this.field = null;
        this.value = null;
        this.validationErrors = new ArrayList<>();
    }

    public SharedExceptionValidation(String field, Object value, String message) {
        super(message);
        this.field = field;
        this.value = value;
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(message);
    }

    public SharedExceptionValidation(String field, Object value, List<String> validationErrors) {
        super(String.join("; ", validationErrors));
        this.field = field;
        this.value = value;
        this.validationErrors = new ArrayList<>(validationErrors);
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }

    public void addError(String error) {
        this.validationErrors.add(error);
    }
}