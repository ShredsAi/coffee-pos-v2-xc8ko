package ai.shreds.product_management_availability_shred_g5ds.application.exceptions;

import java.util.UUID;

public class ApplicationExceptionCategoryNotFound extends RuntimeException {
    
    private final UUID categoryId;
    
    public ApplicationExceptionCategoryNotFound(UUID categoryId) {
        super(String.format("Category with ID %s not found", categoryId));
        this.categoryId = categoryId;
    }
    
    public ApplicationExceptionCategoryNotFound(String message, UUID categoryId) {
        super(message);
        this.categoryId = categoryId;
    }
    
    public ApplicationExceptionCategoryNotFound(String message, UUID categoryId, Throwable cause) {
        super(message, cause);
        this.categoryId = categoryId;
    }
    
    public UUID getCategoryId() {
        return categoryId;
    }
}