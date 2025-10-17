package ai.shreds.product_management_availability_shred_g5ds.adapters.exceptions;

import org.springframework.http.HttpStatus;

public class AdapterExceptionHttp extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    public AdapterExceptionHttp(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = null;
    }

    public AdapterExceptionHttp(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public AdapterExceptionHttp(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = null;
    }

    public AdapterExceptionHttp(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "AdapterExceptionHttp{" +
                "httpStatus=" + httpStatus +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}