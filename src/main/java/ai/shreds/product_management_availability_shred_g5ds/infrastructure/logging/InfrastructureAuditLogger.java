package ai.shreds.product_management_availability_shred_g5ds.infrastructure.logging;

import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueProductId;
import ai.shreds.product_management_availability_shred_g5ds.domain.value_objects.DomainValueLocationId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class InfrastructureAuditLogger {

    private final Logger logger;
    private final ObjectMapper objectMapper;

    @Autowired
    public InfrastructureAuditLogger(
            @Qualifier("auditLogger") Logger logger,
            ObjectMapper objectMapper) {
        this.logger = logger;
        this.objectMapper = objectMapper;
    }

    public void logProductCreated(DomainValueProductId productId, String userId) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "PRODUCT_CREATED");
        auditData.put("productId", productId.getValue().toString());
        auditData.put("userId", userId);
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("action", "CREATE");
        auditData.put("resource", "Product");
        
        logAuditEvent(auditData);
    }

    public void logProductUpdated(DomainValueProductId productId, Map<String, Object> changes, String userId) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "PRODUCT_UPDATED");
        auditData.put("productId", productId.getValue().toString());
        auditData.put("userId", userId);
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("action", "UPDATE");
        auditData.put("resource", "Product");
        auditData.put("changes", changes);
        auditData.put("changedFields", changes.keySet());
        
        logAuditEvent(auditData);
    }

    public void logAvailabilityChanged(DomainValueProductId productId, DomainValueLocationId locationId, Boolean isAvailable) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "AVAILABILITY_CHANGED");
        auditData.put("productId", productId.getValue().toString());
        auditData.put("locationId", locationId.getValue().toString());
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("action", "AVAILABILITY_UPDATE");
        auditData.put("resource", "ProductAvailability");
        auditData.put("newAvailabilityStatus", isAvailable);
        auditData.put("severity", isAvailable ? "INFO" : "WARNING");
        
        logAuditEvent(auditData);
    }

    public void logInventorySync(DomainValueLocationId locationId, boolean success) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "INVENTORY_SYNC");
        auditData.put("locationId", locationId.getValue().toString());
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("action", "SYNC");
        auditData.put("resource", "Inventory");
        auditData.put("success", success);
        auditData.put("severity", success ? "INFO" : "ERROR");
        
        logAuditEvent(auditData);
    }
    
    public void logCategoryCreated(String categoryId, String categoryName, String userId) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "CATEGORY_CREATED");
        auditData.put("categoryId", categoryId);
        auditData.put("categoryName", categoryName);
        auditData.put("userId", userId);
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("action", "CREATE");
        auditData.put("resource", "Category");
        
        logAuditEvent(auditData);
    }
    
    public void logVariantCreated(String variantId, DomainValueProductId productId, String sizeName, String userId) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "VARIANT_CREATED");
        auditData.put("variantId", variantId);
        auditData.put("productId", productId.getValue().toString());
        auditData.put("sizeName", sizeName);
        auditData.put("userId", userId);
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("action", "CREATE");
        auditData.put("resource", "ProductVariant");
        
        logAuditEvent(auditData);
    }
    
    public void logDataAccess(String operation, String entityType, String entityId, String userId) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", "DATA_ACCESS");
        auditData.put("operation", operation);
        auditData.put("entityType", entityType);
        auditData.put("entityId", entityId);
        auditData.put("userId", userId);
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("action", "ACCESS");
        auditData.put("resource", entityType);
        
        logAuditEvent(auditData);
    }
    
    public void logSystemEvent(String event, String description, Map<String, Object> additionalData) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", event);
        auditData.put("description", description);
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("action", "SYSTEM_EVENT");
        auditData.put("resource", "System");
        
        if (additionalData != null) {
            auditData.putAll(additionalData);
        }
        
        logAuditEvent(auditData);
    }
    
    public void logSecurityEvent(String event, String userId, String ipAddress, boolean success) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("event", event);
        auditData.put("userId", userId);
        auditData.put("ipAddress", ipAddress);
        auditData.put("success", success);
        auditData.put("timestamp", LocalDateTime.now().toString());
        auditData.put("action", "SECURITY_EVENT");
        auditData.put("resource", "Authentication");
        auditData.put("severity", success ? "INFO" : "WARNING");
        
        logAuditEvent(auditData);
    }
    
    private void logAuditEvent(Map<String, Object> auditData) {
        try {
            String jsonAuditLog = objectMapper.writeValueAsString(auditData);
            logger.info(jsonAuditLog);
        } catch (Exception e) {
            // Fallback to simple string logging if JSON serialization fails
            logger.error("Failed to serialize audit data to JSON, logging as string: {}", auditData.toString());
            logger.info("AUDIT_EVENT: {}", auditData.toString());
        }
    }
}