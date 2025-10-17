package ai.shreds.product_management_availability_shred_g5ds.infrastructure.exceptions;

public class InfrastructureExceptionExternalService extends RuntimeException {
    private final String serviceName;
    private final Integer httpStatus;

    public InfrastructureExceptionExternalService(String serviceName, Integer httpStatus) {
        super(String.format("External service call failed - Service: %s, HTTP Status: %d", serviceName, httpStatus));
        this.serviceName = serviceName;
        this.httpStatus = httpStatus;
    }

    public InfrastructureExceptionExternalService(String message, String serviceName, Integer httpStatus) {
        super(message);
        this.serviceName = serviceName;
        this.httpStatus = httpStatus;
    }

    public InfrastructureExceptionExternalService(String message, Throwable cause, String serviceName, Integer httpStatus) {
        super(message, cause);
        this.serviceName = serviceName;
        this.httpStatus = httpStatus;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }
}