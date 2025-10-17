package ai.shreds.product_management_availability_shred_g5ds.adapters.primary;

import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductCreated;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductUpdated;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventAvailabilityChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AdapterEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(AdapterEventPublisher.class);
    
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public AdapterEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishProductCreatedEvent(DomainEventProductCreated event) {
        try {
            logger.info("Publishing ProductCreated event for product: {} with name: {}", 
                       event.getProductId(), event.getProductName());
            
            applicationEventPublisher.publishEvent(event);
            
            logger.debug("Successfully published ProductCreated event for product: {}", 
                        event.getProductId());
        } catch (Exception e) {
            logger.error("Error publishing ProductCreated event for product: {}", 
                        event.getProductId(), e);
            throw new RuntimeException("Failed to publish ProductCreated event", e);
        }
    }

    public void publishProductUpdatedEvent(DomainEventProductUpdated event) {
        try {
            logger.info("Publishing ProductUpdated event for product: {} with name: {}", 
                       event.getProductId(), event.getProductName());
            
            if (logger.isDebugEnabled()) {
                logger.debug("ProductUpdated event changes: {}", event.getChanges());
            }
            
            applicationEventPublisher.publishEvent(event);
            
            logger.debug("Successfully published ProductUpdated event for product: {}", 
                        event.getProductId());
        } catch (Exception e) {
            logger.error("Error publishing ProductUpdated event for product: {}", 
                        event.getProductId(), e);
            throw new RuntimeException("Failed to publish ProductUpdated event", e);
        }
    }

    public void publishAvailabilityChangedEvent(DomainEventAvailabilityChanged event) {
        try {
            logger.info("Publishing AvailabilityChanged event for product: {} at location: {}, available: {}", 
                       event.getProductId(), event.getLocationId(), event.getIsAvailable());
            
            if (logger.isDebugEnabled()) {
                logger.debug("AvailabilityChanged event details - quantity: {}, reason: {}, restock time: {}", 
                           event.getEstimatedQuantity(), 
                           event.getUnavailableReason(), 
                           event.getEstimatedRestockTime());
            }
            
            applicationEventPublisher.publishEvent(event);
            
            logger.debug("Successfully published AvailabilityChanged event for product: {} at location: {}", 
                        event.getProductId(), event.getLocationId());
        } catch (Exception e) {
            logger.error("Error publishing AvailabilityChanged event for product: {} at location: {}", 
                        event.getProductId(), event.getLocationId(), e);
            throw new RuntimeException("Failed to publish AvailabilityChanged event", e);
        }
    }

    /**
     * Generic method to publish any domain event
     * Can be used for future extensibility
     */
    public void publishEvent(Object event) {
        try {
            logger.debug("Publishing generic domain event of type: {}", event.getClass().getSimpleName());
            
            applicationEventPublisher.publishEvent(event);
            
            logger.debug("Successfully published generic domain event of type: {}", 
                        event.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Error publishing generic domain event of type: {}", 
                        event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish domain event", e);
        }
    }
}