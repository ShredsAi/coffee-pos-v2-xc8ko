package ai.shreds.product_management_availability_shred_g5ds.domain.exceptions;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;

/**
 * Domain exception thrown when a requested category is not found.
 * Contains the category ID that was not found for debugging and logging purposes.
 */
public class DomainExceptionCategoryNotFound extends RuntimeException {
    private final DomainValueCategoryId categoryId;

    /**
     * Constructs a new CategoryNotFound exception with the specified category ID.
     * @param categoryId the ID of the category that was not found
     */
    public DomainExceptionCategoryNotFound(DomainValueCategoryId categoryId) {
        super(String.format("Category with ID '%s' was not found", categoryId));
        this.categoryId = categoryId;
    }

    /**
     * Constructs a new CategoryNotFound exception with custom message and category ID.
     * @param message the custom error message
     * @param categoryId the ID of the category that was not found
     */
    public DomainExceptionCategoryNotFound(String message, DomainValueCategoryId categoryId) {
        super(message);
        this.categoryId = categoryId;
    }

    /**
     * Gets the category ID that was not found.
     * @return the category ID
     */
    public DomainValueCategoryId getCategoryId() {
        return categoryId;
    }

    @Override
    public String toString() {
        return String.format("DomainExceptionCategoryNotFound{categoryId=%s, message='%s'}", 
            categoryId, getMessage());
    }
}