package ai.shreds.product_management_availability_shred_g5ds.adapters.exceptions;

import ai.shreds.product_management_availability_shred_g5ds.shared.SharedExceptionValidation;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedExceptionNotFound;
import ai.shreds.product_management_availability_shred_g5ds.shared.SharedExceptionBusinessRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class AdapterExceptionGlobalHandler {

    private static final Logger logger = LoggerFactory.getLogger(AdapterExceptionGlobalHandler.class);

    @ExceptionHandler(SharedExceptionValidation.class)
    public ResponseEntity<AdapterErrorResponse> handleValidationException(
            SharedExceptionValidation exception, WebRequest request) {
        
        logger.warn("Validation exception occurred: {}", exception.getMessage());
        
        AdapterErrorResponse errorResponse = new AdapterErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        // Add field-specific validation errors if available
        if (exception.getField() != null) {
            List<AdapterFieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new AdapterFieldError(
                    exception.getField(),
                    exception.getValue(),
                    exception.getMessage()
            ));
            errorResponse.setFieldErrors(fieldErrors);
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(SharedExceptionNotFound.class)
    public ResponseEntity<AdapterErrorResponse> handleNotFoundException(
            SharedExceptionNotFound exception, WebRequest request) {
        
        logger.info("Resource not found: {} with ID: {}", 
                   exception.getResourceType(), exception.getResourceId());
        
        AdapterErrorResponse errorResponse = new AdapterErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(SharedExceptionBusinessRule.class)
    public ResponseEntity<AdapterErrorResponse> handleBusinessRuleException(
            SharedExceptionBusinessRule exception, WebRequest request) {
        
        logger.warn("Business rule violation: {} - {}", 
                   exception.getRuleName(), exception.getViolatedConstraint());
        
        AdapterErrorResponse errorResponse = new AdapterErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Business Rule Violation",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AdapterErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, WebRequest request) {
        
        logger.warn("Method argument validation failed: {}", exception.getMessage());
        
        AdapterErrorResponse errorResponse = new AdapterErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Invalid request data provided",
                request.getDescription(false).replace("uri=", "")
        );
        
        // Extract field errors from validation exception
        List<AdapterFieldError> fieldErrors = new ArrayList<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            fieldErrors.add(new AdapterFieldError(
                    fieldError.getField(),
                    fieldError.getRejectedValue(),
                    fieldError.getDefaultMessage()
            ));
        });
        errorResponse.setFieldErrors(fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AdapterErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception, WebRequest request) {
        
        logger.warn("Constraint validation failed: {}", exception.getMessage());
        
        AdapterErrorResponse errorResponse = new AdapterErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Constraint Violation",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AdapterExceptionHttp.class)
    public ResponseEntity<AdapterErrorResponse> handleAdapterHttpException(
            AdapterExceptionHttp exception, WebRequest request) {
        
        logger.error("HTTP adapter exception: {}", exception.getMessage());
        
        AdapterErrorResponse errorResponse = new AdapterErrorResponse(
                exception.getHttpStatus().value(),
                exception.getHttpStatus().getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(exception.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AdapterErrorResponse> handleGenericException(
            Exception exception, WebRequest request) {
        
        logger.error("Unexpected error occurred", exception);
        
        AdapterErrorResponse errorResponse = new AdapterErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AdapterErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception, WebRequest request) {
        
        logger.warn("Invalid argument provided: {}", exception.getMessage());
        
        AdapterErrorResponse errorResponse = new AdapterErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                exception.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}