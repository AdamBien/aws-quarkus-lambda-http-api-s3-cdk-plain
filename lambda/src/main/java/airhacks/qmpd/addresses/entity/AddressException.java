package airhacks.qmpd.addresses.entity;

import jakarta.json.Json;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base exception for address management operations.
 * 
 * Serves as the parent class for all address-related exceptions
 * providing common error handling functionality with self-contained Response building.
 * Extends WebApplicationException per MicroProfile guidelines.
 */
public class AddressException extends WebApplicationException {
    
    public AddressException(String message) {
        super(message, createErrorResponse(message, Response.Status.INTERNAL_SERVER_ERROR));
    }
    
    public AddressException(String message, Throwable cause) {
        super(message, cause, createErrorResponse(message, Response.Status.INTERNAL_SERVER_ERROR));
    }
    
    public AddressException(String message, Response.Status status) {
        super(message, createErrorResponse(message, status));
    }
    
    public AddressException(String message, Throwable cause, Response.Status status) {
        super(message, cause, createErrorResponse(message, status));
    }
    
    private static Response createErrorResponse(String message, Response.Status status) {
        var errorResponse = Json.createObjectBuilder()
            .add("error", "ADDRESS_ERROR")
            .add("message", message)
            .add("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
        
        return Response.status(status)
            .entity(errorResponse)
            .build();
    }
}