package ai.shreds.product_management_availability_shred_g5ds.domain.ports;

import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductCreated;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventProductUpdated;
import ai.shreds.product_management_availability_shred_g5ds.domain.DomainEventAvailabilityChanged;

/**
 * Domain output port for publishing domain events.
 * Defines the contract for event publishing to external systems and internal components.
 */
public interface DomainOutputPortEventPublisher {
    
    /**
     * Publishes a product created event.
     * @param event the product created event to publish
     */
    void publishProductCreatedEvent(DomainEventProductCreated event);
    
    /**
     * Publishes a product updated event.
     * @param event the product updated event to publish
     */
    void publishProductUpdatedEvent(DomainEventProductUpdated event);
    
    /**
     * Publishes an availability changed event.
     * @param event the availability changed event to publish
     */
    void publishAvailabilityChangedEvent(DomainEventAvailabilityChanged event);
}