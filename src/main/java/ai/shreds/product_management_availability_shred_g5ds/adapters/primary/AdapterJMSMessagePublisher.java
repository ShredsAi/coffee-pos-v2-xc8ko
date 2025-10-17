package ai.shreds.product_management_availability_shred_g5ds.adapters.primary;

import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventAvailabilityChanged;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductCreated;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductUpdated;
import ai.shreds.product_management_availability_shred_g5ds.shared.utils.SharedUtilMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class AdapterJMSMessagePublisher {

    private static final Logger logger = LoggerFactory.getLogger(AdapterJMSMessagePublisher.class);
    
    private final JmsTemplate jmsTemplate;
    private final SharedUtilMapper messageMapper;

    // Queue destinations
    private static final String AVAILABILITY_CHANGED_QUEUE = "product.availability.changes";
    private static final String PRODUCT_CREATED_QUEUE = "product.lifecycle.created";
    private static final String PRODUCT_UPDATED_QUEUE = "product.lifecycle.updated";

    @Autowired
    public AdapterJMSMessagePublisher(JmsTemplate jmsTemplate, SharedUtilMapper messageMapper) {
        this.jmsTemplate = jmsTemplate;
        this.messageMapper = messageMapper;
    }

    public void publishAvailabilityChangedMessage(DomainEventAvailabilityChanged availabilityChangedEvent) {
        try {
            logger.info("Publishing availability changed message for product: {} at location: {}", 
                       availabilityChangedEvent.getProductId(), 
                       availabilityChangedEvent.getLocationId());
            
            Map<String, Object> messagePayload = createAvailabilityChangedPayload(availabilityChangedEvent);
            String messageJson = messageMapper.toJson(messagePayload);
            
            jmsTemplate.send(AVAILABILITY_CHANGED_QUEUE, session -> {
                Message message = session.createTextMessage(messageJson);
                message.setStringProperty("messageType", "AVAILABILITY_CHANGED");
                message.setStringProperty("productId", availabilityChangedEvent.getProductId().toString());
                message.setStringProperty("locationId", availabilityChangedEvent.getLocationId().toString());
                message.setLongProperty("timestamp", System.currentTimeMillis());
                return message;
            });
            
            logger.info("Successfully published availability changed message");
        } catch (Exception e) {
            logger.error("Error publishing availability changed message", e);
            throw new RuntimeException("Failed to publish availability changed message", e);
        }
    }

    public void publishProductCreatedMessage(DomainEventProductCreated productCreatedEvent) {
        try {
            logger.info("Publishing product created message for product: {}", 
                       productCreatedEvent.getProductId());
            
            Map<String, Object> messagePayload = createProductCreatedPayload(productCreatedEvent);
            String messageJson = messageMapper.toJson(messagePayload);
            
            jmsTemplate.send(PRODUCT_CREATED_QUEUE, session -> {
                Message message = session.createTextMessage(messageJson);
                message.setStringProperty("messageType", "PRODUCT_CREATED");
                message.setStringProperty("productId", productCreatedEvent.getProductId().toString());
                message.setStringProperty("productType", productCreatedEvent.getProductType().toString());
                message.setLongProperty("timestamp", System.currentTimeMillis());
                return message;
            });
            
            logger.info("Successfully published product created message");
        } catch (Exception e) {
            logger.error("Error publishing product created message", e);
            throw new RuntimeException("Failed to publish product created message", e);
        }
    }

    public void publishProductUpdatedMessage(DomainEventProductUpdated productUpdatedEvent) {
        try {
            logger.info("Publishing product updated message for product: {}", 
                       productUpdatedEvent.getProductId());
            
            Map<String, Object> messagePayload = createProductUpdatedPayload(productUpdatedEvent);
            String messageJson = messageMapper.toJson(messagePayload);
            
            jmsTemplate.send(PRODUCT_UPDATED_QUEUE, session -> {
                Message message = session.createTextMessage(messageJson);
                message.setStringProperty("messageType", "PRODUCT_UPDATED");
                message.setStringProperty("productId", productUpdatedEvent.getProductId().toString());
                message.setLongProperty("timestamp", System.currentTimeMillis());
                return message;
            });
            
            logger.info("Successfully published product updated message");
        } catch (Exception e) {
            logger.error("Error publishing product updated message", e);
            throw new RuntimeException("Failed to publish product updated message", e);
        }
    }

    private Map<String, Object> createAvailabilityChangedPayload(DomainEventAvailabilityChanged event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messageType", "AVAILABILITY_CHANGED");
        payload.put("productId", event.getProductId().toString());
        payload.put("locationId", event.getLocationId().toString());
        payload.put("isAvailable", event.getIsAvailable());
        payload.put("estimatedQuantity", event.getEstimatedQuantity());
        
        if (event.getUnavailableReason() != null) {
            payload.put("unavailableReason", event.getUnavailableReason().toString());
        }
        if (event.getEstimatedRestockTime() != null) {
            payload.put("estimatedRestockTime", event.getEstimatedRestockTime().toString());
        }
        
        payload.put("timestamp", event.getTimestamp().toString());
        return payload;
    }

    private Map<String, Object> createProductCreatedPayload(DomainEventProductCreated event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messageType", "PRODUCT_CREATED");
        payload.put("productId", event.getProductId().toString());
        payload.put("productName", event.getProductName());
        payload.put("categoryId", event.getCategoryId().toString());
        payload.put("productType", event.getProductType().toString());
        payload.put("timestamp", event.getTimestamp().toString());
        return payload;
    }

    private Map<String, Object> createProductUpdatedPayload(DomainEventProductUpdated event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messageType", "PRODUCT_UPDATED");
        payload.put("productId", event.getProductId().toString());
        payload.put("productName", event.getProductName());
        payload.put("changes", event.getChanges());
        payload.put("timestamp", event.getTimestamp().toString());
        return payload;
    }
}