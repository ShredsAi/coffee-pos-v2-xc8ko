package ai.shreds.product_management_availability_shred_g5ds.application.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductCreated;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductUpdated;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventAvailabilityChanged;

public interface ApplicationOutputPortEventPublisher {
    
    /**
     * Publishes a product created event to external systems and message queues
     * @param event The product created domain event
     */
    void publishProductCreatedEvent(DomainEventProductCreated event);
    
    /**
     * Publishes a product updated event to external systems and message queues
     * @param event The product updated domain event
     */
    void publishProductUpdatedEvent(DomainEventProductUpdated event);
    
    /**
     * Publishes an availability changed event to external systems and message queues
     * @param event The availability changed domain event
     */
    void publishAvailabilityChangedEvent(DomainEventAvailabilityChanged event);
}