package ai.shreds.product_management_availability_shred_g5ds.adapters.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdapterErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private Integer status;
    private String error;
    private String message;
    private String path;
    private List<AdapterFieldError> fieldErrors;

    public AdapterErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public AdapterErrorResponse(Integer status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public AdapterErrorResponse(Integer status, String error, String message, String path, 
                               List<AdapterFieldError> fieldErrors) {
        this(status, error, message, path);
        this.fieldErrors = fieldErrors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<AdapterFieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<AdapterFieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    @Override
    public String toString() {
        return "AdapterErrorResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", fieldErrors=" + fieldErrors +
                '}';
    }
}