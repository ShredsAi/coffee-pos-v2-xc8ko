package ai.shreds.product_management_availability_shred_g5ds.adapters.exceptions;

public class AdapterExceptionJMS extends RuntimeException {

    private final String queueName;
    private final String messageId;

    public AdapterExceptionJMS(String message) {
        super(message);
        this.queueName = null;
        this.messageId = null;
    }

    public AdapterExceptionJMS(String message, String queueName, String messageId) {
        super(message);
        this.queueName = queueName;
        this.messageId = messageId;
    }

    public AdapterExceptionJMS(String message, Throwable cause) {
        super(message, cause);
        this.queueName = null;
        this.messageId = null;
    }

    public AdapterExceptionJMS(String message, Throwable cause, String queueName, String messageId) {
        super(message, cause);
        this.queueName = queueName;
        this.messageId = messageId;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return "AdapterExceptionJMS{" +
                "queueName='" + queueName + '\'' +
                ", messageId='" + messageId + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}