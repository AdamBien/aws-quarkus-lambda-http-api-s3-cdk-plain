package airhacks.qmpd.addresses.entity;

import jakarta.json.Json;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Exception for address validation errors.
 * 
 * Contains field-specific validation errors with self-contained Response building.
 * Extends BadRequestException per MicroProfile guidelines.
 */
public class AddressValidationException extends BadRequestException {
    
    private final List<ValidationError> validationErrors;
    
    public AddressValidationException(List<ValidationError> validationErrors) {
        super(createMessage(validationErrors), createValidationErrorResponse(validationErrors));
        this.validationErrors = List.copyOf(validationErrors);
    }
    
    public AddressValidationException(String field, String message) {
        this(List.of(new ValidationError(field, message)));
    }
    
    /**
     * Gets the list of validation errors.
     * 
     * @return immutable list of validation errors
     */
    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }
    
    /**
     * Gets validation errors grouped by field name.
     * 
     * @return map of field names to their error messages
     */
    public Map<String, List<String>> getErrorsByField() {
        return validationErrors.stream()
            .collect(Collectors.groupingBy(
                ValidationError::field,
                Collectors.mapping(ValidationError::message, Collectors.toList())
            ));
    }
    
    /**
     * Checks if there are validation errors for a specific field.
     * 
     * @param fieldName the field name to check
     * @return true if the field has validation errors
     */
    public boolean hasErrorsForField(String fieldName) {
        return validationErrors.stream()
            .anyMatch(error -> error.field().equals(fieldName));
    }
    
    private static String createMessage(List<ValidationError> errors) {
        if (errors.isEmpty()) {
            return "Address validation failed";
        }
        
        var errorCount = errors.size();
        var firstError = errors.get(0);
        
        if (errorCount == 1) {
            return "Address validation failed: " + firstError.message();
        }
        
        return String.format("Address validation failed with %d errors: %s and %d more", 
            errorCount, firstError.message(), errorCount - 1);
    }
    
    private static Response createValidationErrorResponse(List<ValidationError> errors) {
        var detailsBuilder = Json.createArrayBuilder();
        for (var error : errors) {
            var detail = Json.createObjectBuilder()
                .add("field", error.field())
                .add("message", error.message())
                .build();
            detailsBuilder.add(detail);
        }
        
        var errorResponse = Json.createObjectBuilder()
            .add("error", "VALIDATION_ERROR")
            .add("message", "Address validation failed")
            .add("details", detailsBuilder.build())
            .add("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
        
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(errorResponse)
            .build();
    }
    
    /**
     * Represents a validation error for a specific field.
     */
    public record ValidationError(String field, String message) {}
}