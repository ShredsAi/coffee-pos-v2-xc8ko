package ai.shreds.product_management_availability_shred_g5ds.infrastructure.external_services;

import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductCreated;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductUpdated;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventAvailabilityChanged;
import ai.shreds.product_management_availability_shred_g5ds.domain.ports.DomainOutputPortEventPublisher;
import ai.shreds.product_management_availability_shred_g5ds.infrastructure.exceptions.InfrastructureExceptionMessaging;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InfrastructureClientEventPublisher implements DomainOutputPortEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureClientEventPublisher.class);
    
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    
    // JMS Queue names
    private static final String PRODUCT_CREATED_QUEUE = "product.created";
    private static final String PRODUCT_UPDATED_QUEUE = "product.updated";
    private static final String AVAILABILITY_CHANGED_QUEUE = "product.availability.changes";

    @Autowired
    public InfrastructureClientEventPublisher(
            ApplicationEventPublisher applicationEventPublisher,
            JmsTemplate jmsTemplate,
            ObjectMapper objectMapper) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishProductCreatedEvent(DomainEventProductCreated event) {
        if (event == null) {
            logger.warn("Attempted to publish null ProductCreated event");
            return;
        }
        
        try {
            logger.info("Publishing ProductCreated event for product: {}", event.getProductId().getValue());
            
            // Publish to Spring Application Context
            publishToSpringEvents(event);
            
            // Publish to JMS Queue
            Map<String, Object> payload = createProductCreatedPayload(event);
            publishToJMS(payload, PRODUCT_CREATED_QUEUE);
            
            logger.debug("Successfully published ProductCreated event for product: {}", event.getProductId().getValue());
            
        } catch (Exception e) {
            logger.error("Failed to publish ProductCreated event for product: {}", event.getProductId().getValue(), e);
            throw new InfrastructureExceptionMessaging(
                "Failed to publish ProductCreated event", e, PRODUCT_CREATED_QUEUE, "ProductCreatedEvent"
            );
        }
    }

    @Override
    public void publishProductUpdatedEvent(DomainEventProductUpdated event) {
        if (event == null) {
            logger.warn("Attempted to publish null ProductUpdated event");
            return;
        }
        
        try {
            logger.info("Publishing ProductUpdated event for product: {}", event.getProductId().getValue());
            
            // Publish to Spring Application Context
            publishToSpringEvents(event);
            
            // Publish to JMS Queue
            Map<String, Object> payload = createProductUpdatedPayload(event);
            publishToJMS(payload, PRODUCT_UPDATED_QUEUE);
            
            logger.debug("Successfully published ProductUpdated event for product: {}", event.getProductId().getValue());
            
        } catch (Exception e) {
            logger.error("Failed to publish ProductUpdated event for product: {}", event.getProductId().getValue(), e);
            throw new InfrastructureExceptionMessaging(
                "Failed to publish ProductUpdated event", e, PRODUCT_UPDATED_QUEUE, "ProductUpdatedEvent"
            );
        }
    }

    @Override
    public void publishAvailabilityChangedEvent(DomainEventAvailabilityChanged event) {
        if (event == null) {
            logger.warn("Attempted to publish null AvailabilityChanged event");
            return;
        }
        
        try {
            logger.info("Publishing AvailabilityChanged event for product: {} at location: {}", 
                event.getProductId().getValue(), event.getLocationId().getValue());
            
            // Publish to Spring Application Context
            publishToSpringEvents(event);
            
            // Publish to JMS Queue
            Map<String, Object> payload = createAvailabilityChangedPayload(event);
            publishToJMS(payload, AVAILABILITY_CHANGED_QUEUE);
            
            logger.debug("Successfully published AvailabilityChanged event for product: {} at location: {}", 
                event.getProductId().getValue(), event.getLocationId().getValue());
            
        } catch (Exception e) {
            logger.error("Failed to publish AvailabilityChanged event for product: {} at location: {}", 
                event.getProductId().getValue(), event.getLocationId().getValue(), e);
            throw new InfrastructureExceptionMessaging(
                "Failed to publish AvailabilityChanged event", e, AVAILABILITY_CHANGED_QUEUE, "AvailabilityChangedEvent"
            );
        }
    }

    private Map<String, Object> createProductCreatedPayload(DomainEventProductCreated event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "ProductCreated");
        payload.put("productId", event.getProductId().getValue().toString());
        payload.put("productName", event.getProductName());
        payload.put("categoryId", event.getCategoryId().getValue().toString());
        payload.put("productType", event.getProductType().name());
        payload.put("timestamp", event.getTimestamp().toString());
        return payload;
    }

    private Map<String, Object> createProductUpdatedPayload(DomainEventProductUpdated event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "ProductUpdated");
        payload.put("productId", event.getProductId().getValue().toString());
        payload.put("productName", event.getProductName());
        payload.put("changes", event.getChanges());
        payload.put("timestamp", event.getTimestamp().toString());
        return payload;
    }

    private Map<String, Object> createAvailabilityChangedPayload(DomainEventAvailabilityChanged event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messageType", "AvailabilityChanged");
        payload.put("productId", event.getProductId().getValue().toString());
        payload.put("locationId", event.getLocationId().getValue().toString());
        payload.put("isAvailable", event.getIsAvailable());
        payload.put("estimatedQuantity", event.getEstimatedQuantity());
        
        if (event.getUnavailableReason() != null) {
            payload.put("unavailableReason", event.getUnavailableReason().name());
        }
        
        if (event.getEstimatedRestockTime() != null) {
            payload.put("estimatedRestockTime", event.getEstimatedRestockTime().toString());
        }
        
        payload.put("timestamp", event.getTimestamp().toString());
        return payload;
    }

    private void publishToJMS(Object payload, String destination) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            jmsTemplate.convertAndSend(destination, jsonPayload);
            logger.debug("Successfully sent message to JMS queue: {}", destination);
        } catch (Exception e) {
            logger.error("Failed to publish message to JMS queue: {}", destination, e);
            throw new InfrastructureExceptionMessaging(
                "Failed to publish to JMS queue", e, destination, payload.getClass().getSimpleName()
            );
        }
    }

    private void publishToSpringEvents(Object event) {
        try {
            applicationEventPublisher.publishEvent(event);
            logger.debug("Successfully published event to Spring Application Context: {}", event.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Failed to publish event to Spring Application Context: {}", event.getClass().getSimpleName(), e);
            // Don't throw exception here as JMS publishing is more critical
        }
    }
}