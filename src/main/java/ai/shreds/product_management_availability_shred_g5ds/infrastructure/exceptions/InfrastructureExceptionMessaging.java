package ai.shreds.product_management_availability_shred_g5ds.infrastructure.exceptions;

public class InfrastructureExceptionMessaging extends RuntimeException {
    private final String queueName;
    private final String messageType;

    public InfrastructureExceptionMessaging(String queueName, String messageType) {
        super(String.format("Messaging operation failed - Queue: %s, MessageType: %s", queueName, messageType));
        this.queueName = queueName;
        this.messageType = messageType;
    }

    public InfrastructureExceptionMessaging(String message, String queueName, String messageType) {
        super(message);
        this.queueName = queueName;
        this.messageType = messageType;
    }

    public InfrastructureExceptionMessaging(String message, Throwable cause, String queueName, String messageType) {
        super(message, cause);
        this.queueName = queueName;
        this.messageType = messageType;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getMessageType() {
        return messageType;
    }
}