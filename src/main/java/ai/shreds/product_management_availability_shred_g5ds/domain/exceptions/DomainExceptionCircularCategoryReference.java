package ai.shreds.product_management_availability_shred_g5ds.domain.exceptions;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueCategoryId;

/**
 * Domain exception thrown when a circular reference would be created in the category hierarchy.
 * Contains the category IDs that would create the circular reference.
 */
public class DomainExceptionCircularCategoryReference extends RuntimeException {
    private final DomainValueCategoryId categoryId;
    private final DomainValueCategoryId parentCategoryId;

    /**
     * Constructs a new CircularCategoryReference exception.
     * @param categoryId the category that would create the circular reference
     * @param parentCategoryId the proposed parent category
     */
    public DomainExceptionCircularCategoryReference(DomainValueCategoryId categoryId, 
                                                   DomainValueCategoryId parentCategoryId) {
        super(String.format("Circular reference detected: Category '%s' cannot have parent '%s' as it would create a cycle", 
                categoryId, parentCategoryId));
        this.categoryId = categoryId;
        this.parentCategoryId = parentCategoryId;
    }

    /**
     * Constructs a new CircularCategoryReference exception with custom message.
     * @param message the custom error message
     * @param categoryId the category that would create the circular reference
     * @param parentCategoryId the proposed parent category
     */
    public DomainExceptionCircularCategoryReference(String message, DomainValueCategoryId categoryId, 
                                                   DomainValueCategoryId parentCategoryId) {
        super(message);
        this.categoryId = categoryId;
        this.parentCategoryId = parentCategoryId;
    }

    /**
     * Gets the category ID that would create the circular reference.
     * @return the category ID
     */
    public DomainValueCategoryId getCategoryId() {
        return categoryId;
    }

    /**
     * Gets the parent category ID that would create the circular reference.
     * @return the parent category ID
     */
    public DomainValueCategoryId getParentCategoryId() {
        return parentCategoryId;
    }

    @Override
    public String toString() {
        return String.format("DomainExceptionCircularCategoryReference{categoryId=%s, parentCategoryId=%s, message='%s'}", 
            categoryId, parentCategoryId, getMessage());
    }
}